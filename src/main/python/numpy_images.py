'''
import os
import numpy as np
import pandas as pd
import tensorflow as tf
from tensorflow.keras.applications import VGG16
from tensorflow.keras.preprocessing import image

from keras.applications import VGG16
from keras.applications.vgg16 import preprocess_input, decode_predictions

from tensorflow.keras.applications.vgg16 import preprocess_input
from tensorflow.keras.models import Model
from tensorflow.keras.layers import Flatten
from tensorflow.keras.applications.vgg16 import decode_predictions
import os
from PIL import Image
from scipy.spatial import distance


import os
import numpy as np
from tensorflow.keras.applications import VGG16
from tensorflow.keras.preprocessing import image
from tensorflow.keras.models import Model
from tensorflow.keras.layers import Flatten
from tensorflow.keras.applications.vgg16 import preprocess_input, decode_predictions


from tensorflow.keras.applications import VGG16
'''

'''

import cv2
import numpy as np
import os
from keras.applications.vgg16 import VGG16, preprocess_input
from keras.preprocessing import image
from keras.models import Model

# VGG16 모델 불러오기
base_model = VGG16(weights='imagenet')
model = Model(inputs=base_model.input, outputs=base_model.get_layer('fc2').output)

# 이미지를 임베딩하는 함수
def image_to_embedding(image_path, model):
    img = image.load_img(image_path, target_size=(224, 224))
    x = image.img_to_array(img)
    x = np.expand_dims(x, axis=0)
    x = preprocess_input(x)
    return model.predict(x).flatten()


# 이미지 데이터베이스 구성 (디렉토리 내의 이미지 임베딩 저장)
image_dir = 'C:\\Users\\82105\\OneDrive\\바탕 화면\\Case\\src\\main\\resources\\static\\images\\Animation'
database = {}

for filename in os.listdir(image_dir):
    if filename.endswith(".jpg"):
        image_path = os.path.join(image_dir, filename)
        database[filename] = image_to_embedding(image_path, model)

# 파일명을 기준으로 정렬된 이미지 데이터베이스 생성
s_database = dict(sorted(database.items(), key=lambda item: int(item[0].split('.')[0])))
db = s_database

# database를 NumPy 배열로 저장
db_array = np.array(list(db.values()))

# 'data' 폴더가 없으면 생성
data_dir = os.path.join(os.getcwd(), 'src', 'main', 'python', 'data')
if not os.path.exists(data_dir):
    os.makedirs(data_dir)

# Animation.npy 파일로 저장 ('data' 폴더 내에)
save_path = os.path.join(data_dir, 'Animation.npy')
np.save(save_path, db_array)



print(f"Animation.npy 파일이 {data_dir}에 저장되었습니다.")
'''