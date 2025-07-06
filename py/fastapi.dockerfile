FROM pytorch/pytorch:2.2.0-cuda11.8-cudnn8-runtime

WORKDIR /app
COPY . /app

# ENV FORCE_CUDA="1"
RUN pip install --no-cache-dir -r requirements.txt

CMD ["uvicorn", "main:app",  "--port", "5000"]