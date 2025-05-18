import io
from fastapi import FastAPI, File, UploadFile,HTTPException
import torch
import torch.nn as nn
from fastai.vision.all import *
from pathlib import Path
import tempfile
import imghdr
import shutil
import cv2
from torchvision import transforms
from PIL import Image
from torchvision.models import convnext_tiny
from transforms.my_transforms import GaussianBlurTransform


app = FastAPI()
retina_model = load_learner("/mnt/c/Users/sriha/Documents/My projects/Medical Image Analyser/py/models/retina_convnext_tiny_93K_reexported.pth")
device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
pneumonia_model = convnext_tiny(pretrained=True)
pneumonia_model.classifier[2] = nn.Linear(in_features=768, out_features=2)
pneumonia_model.to(device)
pneumonia_model.load_state_dict(torch.load("/mnt/c/Users/sriha/Documents/My projects/Medical Image Analyser/py/models/Pneumonia_convnext_tiny.pth", weights_only=True))
pneumonia_model.eval()
pneumonia_transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor()
])
    

@app.post("/brainseg")
async def braintumorseg():
    return

@app.post("/pneumoniadetect")
async def pneumoniadetection(file: UploadFile = File(...)):
    contents = await file.read()
    
    try:
        image = Image.open(io.BytesIO(contents)).convert("RGB")
    except Exception:
        raise HTTPException(status_code=400, detail={"error": "Unsupported image format."})

    input_tensor = pneumonia_transform(image).unsqueeze(0).to(device)

    with torch.no_grad():
        output = pneumonia_model(input_tensor)
        prob = torch.nn.functional.softmax(output[0], dim=0)
        pred_idx = prob.argmax().item()
        probability = prob[pred_idx].item()

    return {
        "predicted_class": str(pred_idx),
        "probability": float(probability)
    }

@app.post("/drdetection")
async def diabeticretinadetection(file: UploadFile = File(...)):
    ext = imghdr.what(None, h=await file.read())
    await file.seek(0)
    
    if ext not in ["jpeg", "png", "bmp", "jpg", "tiff"]:
        raise HTTPException(status_code=400,detail= {"error": "Unsupported image format."})
    
    with tempfile.NamedTemporaryFile(delete=False, suffix=f".{ext}") as temp_file:
        shutil.copyfileobj(file.file, temp_file)
        temp_file_path = Path(temp_file.name)
        
    pred_class, pred_idx, probs = retina_model.predict(temp_file_path)
    
    temp_file_path.unlink()
    
    return {
        "predicted_class": str(pred_class),
        "probability": float(probs[pred_idx])
    }