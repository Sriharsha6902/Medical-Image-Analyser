
from fastai.vision.all import *
from transforms.my_transforms import GaussianBlurTransform

learn = load_learner("/mnt/c/Users/sriha/Documents/My projects/Medical Image Analyser/py/models/retina_convnext_tiny_93K.pth")
learn.export("/mnt/c/Users/sriha/Documents/My projects/Medical Image Analyser/py/models/retina_convnext_tiny_93K_reexported.pth")