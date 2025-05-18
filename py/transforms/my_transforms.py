import cv2
from fastai.vision.augment import RandTransform
from fastai.vision.all import *
import numpy as np

class CLAHETransformBW(Transform):
    def __init__(self, clip_limit=2.0, tile_grid_size=(8, 8), p=0.5):
        self.clip_limit = clip_limit
        self.tile_grid_size = tile_grid_size
        self.p = p

    def encodes(self, img: PILImage):
        if np.random.rand() > self.p:
            return img

        img_np = np.array(img.convert("L")) 
        clahe = cv2.createCLAHE(clipLimit=self.clip_limit, tileGridSize=self.tile_grid_size)
        cl = clahe.apply(img_np)

        cl_rgb = cv2.cvtColor(cl, cv2.COLOR_GRAY2RGB)

        return PILImage.create(cl_rgb)
    
class GaussianBlurTransform(RandTransform):
    def _init_(self, p=0.5, ksize=(5,5), sigmaX=0):
        super()._init_(p=p)
        self.ksize = ksize
        self.sigmaX = sigmaX

    def encodes(self, img: PILImage):
        arr = np.array(img)
        arr = cv2.GaussianBlur(arr, self.ksize, self.sigmaX)
        return PILImage.create(arr)