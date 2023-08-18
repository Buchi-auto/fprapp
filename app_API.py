from flask import Flask, request, jsonify

import cv2
import base64
from PIL import Image
from io import BytesIO
import numpy as np
import operator
import warnings
import sys
import multipart
import json
import time
import traceback
import matplotlib as mpl
import matplotlib.cm as cm
import matplotlib.pyplot as plt
import matplotlib.image as mpimg
#from matplotlib import pyplot as plt
#from utils import *
# from fatching_copy import *
#from values import *
#from minutiae import *
#from roc import *
from collections import defaultdict
from fingerprint_enhancer import enhance_Fingerprint
from sklearn.cluster import KMeans
#from sklearn.metrics import accuracy_score, precision_score, f1_score
#from pyeer.eer_info import get_eer_stats
#from pyeer.report import generate_eer_report, export_error_rates
#from pyeer.plot import plot_eer_stats
import fingerprint_enhancer
#from segmentation import *

from PIL import Image
import os
from numpy import asarray
from numpy import save

import scipy.io

from kymatio.numpy import Scattering2D

# def edgedet(img):
#     k= np.array([[-1,0,1], [-1,0,1], [-1,0,1]])
#     gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
#     convolve = cv2.filter2D(gray, -1, k)
#     return convolve
#the imports

#from scipy import signal
#import scipy.io as mat_import
#from scipy.fft import fft, fftfreq
#import matplotlib
# import matplotlib.pyplot as plt
# import matplotlib.image as mpimg
#import numpy as np

#import cv2
#from sklearn.cluster import KMeans

def segmentation(image,spatial_weight,show_images=True):
    dim1 = image.shape[0]; dim2 = image.shape[1];
    image_convert = cv2.cvtColor(image, cv2.COLOR_RGB2YCR_CB)

    na, nb = (dim2, dim1)
    a = np.linspace(1, dim2, na)
    b = np.linspace(1, dim1, nb)
    xb, xa = np.meshgrid(a,b)
    xb = np.reshape(xb,(dim1,dim2,1)); xa = np.reshape(xa,(dim1,dim2,1));
    #plt.imshow(xa); plt.title("Color Transformed Image"); plt.show();
    image_convert_concat = np.concatenate((image_convert,xb,xa),axis = 2)

    image_convert_reshape = np.reshape(image_convert_concat, (dim1*dim2,5))
    image_convert_reshape_mean = np.mean(image_convert_reshape,axis=0)
    image_convert_reshape_sd = np.std(image_convert_reshape,axis=0)

    image_convert_reshape = (image_convert_reshape-image_convert_reshape_mean)/image_convert_reshape_sd
    image_convert_reshape[:,3] = spatial_weight*image_convert_reshape[:,3];
    image_convert_reshape[:,4] = spatial_weight*image_convert_reshape[:,4];

    kmeans = KMeans(n_clusters=2, init='k-means++').fit(image_convert_reshape)
    mask = np.reshape(kmeans.labels_, (dim1,dim2,1))
    if mask[0,0,0] == 1:
        mask = 1-mask

    segmented_image = np.multiply(image,mask)
    if show_images:
        # plt.figure()
        # plt.subplot(1,3,1)
        # plt.imshow(image);
        # plt.title("Original Image");
        # plt.subplot(1,3,2)
        # plt.imshow(mask[:,:,0]);
        # plt.title("Mask Image");
        # plt.subplot(1,3,3)
        # plt.imshow(segmented_image);
        # plt.title("Output Masked Image"); plt.show();
        #not returning the image file
        pass

    return segmented_image

def preprocessing(img1):
    img1_raw = cv2.imread(img1)
    img1_raw_rgb = cv2.cvtColor(img1,cv2.COLOR_BGR2RGB)
    img1_s = segmentation(img1_raw_rgb,0.05,show_images=False)
    #img1_s1 = cv2.cvtColor(img1_s,cv2.COLOR_RGB2GRAY)
    img1_snp = img1_s.astype(np.uint8)
    img1_snpr = cv2.resize(img1_snp, (200,200))
    img1_snprg = cv2.cvtColor(img1_snpr,cv2.COLOR_RGB2GRAY)
    clahe = cv2.createCLAHE(clipLimit =127, tileGridSize=(10,10)) #clahe = cv2.createCLAHE(clipLimit =5, tileGridSize=(4,4))
    cl_img = clahe.apply(img1_snprg)

    ATG_image = cv2.adaptiveThreshold(cl_img,255,cv2.ADAPTIVE_THRESH_GAUSSIAN_C,cv2.THRESH_BINARY,9,5) #ATG_image = cv2.adaptiveThreshold(cl_img,127,cv2.ADAPTIVE_THRESH_GAUSSIAN_C,cv2.THRESH_BINARY,29,10)

    extr = fingerprint_enhancer.enhance_Fingerprint(ATG_image)
    # extr = enhance_image(ATG_image)

    # FeaturesTerminations, FeaturesBifurcations,DispImg = extract_minutiae_features(extr, showResult=True)
    # font = cv2.FONT_HERSHEY_SIMPLEX
    # color = (255, 0, 0);org = (10, 15)
    # DispImg = cv2.putText(DispImg, 'Bifurcations', org, font,0.5, color,2, cv2.LINE_AA)
    # color1 = (0, 0, 255);org1 = (10, 30)
    # DispImg = cv2.putText(DispImg, 'Terminations', org1, font,0.5, color1,2, cv2.LINE_AA)

    # plt.figure(figsize = (10,10))
    # plt.subplot(1,2,1); plt.imshow(img1_raw); plt.title('Input Image')
    # plt.subplot(1,2,2); plt.imshow(extr); plt.title('Enhanced Image')
    # plt.subplot(1,5,2); plt.imshow(cl_img,cmap = 'gray'); plt.title('After CLAHE')
    # plt.subplot(1,5,3); plt.imshow(ATG_image,cmap = 'gray'); plt.title('After Adaptive Thresholding')
    # plt.subplot(1,2,2); plt.imshow(extr,cmap = 'gray'); plt.title('Enhancement')
    # plt.subplot(1,5,5); plt.imshow(DispImg,cmap = 'gray'); plt.title('Minutiae Extraction')
    # plt.show()

    return extr

def coeff(dataset_image):
    src_img = Image.open(dataset_image).convert('L').resize((200, 200))
    src_img = np.array(src_img)
    #print("img shape: ", src_img.shape)

    L = 8
    J = 3
    scattering = Scattering2D(J=J, shape=src_img.shape, L=L, max_order=2)

    src_img_tensor = src_img.astype(np.float32) / 255.

    scat_coeffs = scattering(src_img_tensor)
    print("coeffs shape: ", scat_coeffs.shape)
    scat_coeffs= -scat_coeffs

    len_order_1 = J*L
    scat_coeffs_order_1 = scat_coeffs[1:1+len_order_1, :, :]
    norm_order_1 = mpl.colors.Normalize(scat_coeffs_order_1.min(), scat_coeffs_order_1.max(), clip=True)
    mapper_order_1 = cm.ScalarMappable(norm=norm_order_1, cmap="gray")

    len_order_2 = (J*(J-1)//2)*(L**2)
    scat_coeffs_order_2 = scat_coeffs[1+len_order_1:, :, :]
    norm_order_2 = mpl.colors.Normalize(scat_coeffs_order_2.min(), scat_coeffs_order_2.max(), clip=True)
    mapper_order_2 = cm.ScalarMappable(norm=norm_order_2, cmap="gray")

    window_rows, window_columns = scat_coeffs.shape[1:]
    return scat_coeffs_order_2

def preprocessed_coeff(src_img):
    #img_raw = cv2.imread(src_img)
    img_raw_rgb = src_img
    #img_raw_rgb = cv2.cvtColor(img_raw,cv2.COLOR_BGR2RGB)
    #start = time.time()
    #img_raw = cv2.imdecode(np.frombuffer(src_img.read(), np.uint8), cv2.IMREAD_COLOR)
    #img_raw_rgb = cv2.cvtColor(img_raw,cv2.COLOR_BGR2RGB)
    #stop = time.time()
    #print("valk1 "+str(stop-start))

    img_s = segmentation(img_raw_rgb,0.05,show_images=False)
    #img1_s1 = cv2.cvtColor(img1_s,cv2.COLOR_RGB2GRAY)
    img_snp = img_s.astype(np.uint8)
    img_snpr = cv2.resize(img_snp, (200,200))
    img_snprg = cv2.cvtColor(img_snpr,cv2.COLOR_BGR2GRAY)
    clahe = cv2.createCLAHE(clipLimit =127, tileGridSize=(10,10)) #clahe = cv2.createCLAHE(clipLimit =5, tileGridSize=(4,4))
    cl_img = clahe.apply(img_snprg)

    ATG_image = cv2.adaptiveThreshold(cl_img,255,cv2.ADAPTIVE_THRESH_GAUSSIAN_C,cv2.THRESH_BINARY,5,1) #ATG_image = cv2.adaptiveThreshold(cl_img,127,cv2.ADAPTIVE_THRESH_GAUSSIAN_C,cv2.THRESH_BINARY,29,10)

    #extr = fingerprint_enhancer.enhance_Fingerprint(ATG_image)
    #print("img shape: ", ATG_image.shape)

    # src_img = dataset_image.convert('L').resize((200, 200))
    # src_img = np.array(src_img)
    # print("img shape: ", src_img.shape)
    # original_img = cv2.imread(src_img)
    # preprocessed_img = preprocessing(original_img)
    # preprocessed_img_np = np.array(preprocessed_img)
    # print("img shape: ", preprocessed_img_np.shape)

    L = 8
    J = 3
    scattering = Scattering2D(J=J, shape=ATG_image.shape, L=L, max_order=2)

    src_img_tensor = ATG_image.astype(np.float32) / 255.

    scat_coeffs = scattering(src_img_tensor)
    #print("coeffs shape: ", scat_coeffs.shape)
    scat_coeffs= -scat_coeffs

    len_order_1 = J*L
    scat_coeffs_order_1 = scat_coeffs[1:1+len_order_1, :, :]
    norm_order_1 = mpl.colors.Normalize(scat_coeffs_order_1.min(), scat_coeffs_order_1.max(), clip=True)
    mapper_order_1 = cm.ScalarMappable(norm=norm_order_1, cmap="gray")

    len_order_2 = (J*(J-1)//2)*(L**2)
    scat_coeffs_order_2 = scat_coeffs[1+len_order_1:, :, :]
    norm_order_2 = mpl.colors.Normalize(scat_coeffs_order_2.min(), scat_coeffs_order_2.max(), clip=True)
    mapper_order_2 = cm.ScalarMappable(norm=norm_order_2, cmap="gray")

    window_rows, window_columns = scat_coeffs.shape[1:]

    # plt.figure(figsize = (10,10))
    # plt.subplot(1,2,1); plt.imshow(img_raw_rgb); plt.title('Input Image')
    # plt.subplot(1,2,2); plt.imshow(ATG_image, cmap='gray'); plt.title('Enhanced Image')

    return scat_coeffs_order_2

def verification(img1,img2):
    # img11=preprocessing(cv2.imread(img1))
    # img22=preprocessing(cv2.imread(img2))
    overall_arr = np.zeros(shape=(2,120000))
    img_to_idx = {}
    # arr1 = coeff1(img11)
    # arr2= coeff1(img22)
    arr1 = preprocessed_coeff(img1)
    arr2= preprocessed_coeff(img2)
    #img_to_idx[img[:-4]] = idx

    overall_arr[0] = np.array(arr1.flatten())
    overall_arr[1] = np.array(arr2.flatten())
    dist1 = np.linalg.norm(overall_arr[0] -overall_arr[1])
    #print("distance =",dist1)
    return dist1



app = Flask(__name__)


@app.route("/")
def home():
    return "Hello world"


@app.route("/match", methods=['POST'])
def match():
    print("I am here")
    print("its here")
    bm1 = request.form.get("bm1")
    nnums = base64.decodebytes(bm1.encode('utf-8')) #direct
    print("nnums")
    image = Image.open(BytesIO(nnums)).convert("RGB")
    #image.save("new_file.png")
    #bm1 = str(bm1)
    # bm1 = bm1.replace("\n", "") #remove all spaces
    # nums = base64.decodebytes(bm1.encode('utf-8'))
    # bm1 = np.frombuffer(nums, np.uint8)
    #bm1 = np.frombuffer(nnums, np.uint8)
    #print(np.allclose(bm1, nbm1))
    #im1 = (Image.fromarray(bm1.astype(np.uint8))).convert('RGB')
    bm1 = np.asarray(image)
    # print(bm1)
    print(len(bm1))

    bm2 = request.form.get("bm2")

    #bm2 = bm2.replace("\n", "") #remove all spaces
    nums = base64.decodebytes(bm2.encode('utf-8'))
    image = Image.open(BytesIO(nums)).convert("RGB")
    bm2 = np.asarray(image)
    #image.save("new_file2.png")
    #bm2 = np.frombuffer(nums, np.uint8)
    #im2 = (Image.fromarray(bm2.astype(np.uint8))).resize((500,500))
    # print(bm2)
    print(len(bm2))

    #width = int(request.form.get("bmw"))
    #height = int(request.form.get("bmh"))
    # print(bm1.shape)
    # bm1 = bm1.reshape(height, width, 3)
    # print(bm2.shape)
    # bm2 = bm2.reshape(height, width, 3)

    ans = round(verification(bm1, bm2), 4)
    #ans = round(verification(new_img1, new_img2), 4)
    print("Verification is ", str(ans))
    return jsonify({"distance":str(ans)})
    # here = time.time()

    # print("No error here")


    # try:
    #     #print(str(request.get_data(), 'UTF-8')[:1000])
    #     print(request.headers)
    #     print(request.body)
    #     print(request.form)
    #     #print(json.loads(request.form.get('file1')))
    #     #print(str(request.get_json()))
    # except Exception as e:
    #     print(str(e))
    # #print(str(request.get_data(as_text=True)))
    # try:
    #     #data1 = request.form.get("file1")
    #     print("it came here1")
    #     data11 = request.files['file1']
    #     print(data11.filename())
    #     print("some issue here")
    #     print("data 1", str(data11))
    #     data2 = request.form.get("file2")
    #     img = Image.open(BytesIO(data1))
    #     img2 = Image.open(BytesIO(data2))
    #     ans = round(verification(img, img2), 4)
    #     #there = time.time()
    # #return jsonify({"result":img.filename})
    #     print("I am done")
    #     return jsonify({"distance":str(ans)})
    # except Exception as e:
    #     return jsonify({"distance":str(traceback.format_exc())})
    # # except Exception as e:
    # #     return jsonify({"name1":str(img.filename), "name2":str(img2.filename), "exp":str(traceback.format_exc())})


if __name__ == '__main__':
    app.run(debug=True)