from flask import Flask, render_template, request, jsonify
from scipy.spatial import distance
import numpy as np
from flask_cors import CORS

app = Flask(__name__)
CORS(app)
apiUrl = "http://localhost:8081/casebycase/submitSelectedImages"


array_ani = np.load('C:\\Users\\82105\\OneDrive\\바탕 화면\\Case\\src\\main\\python\\src\\main\\python\\data\\Animation.npy')
# 나머지 이미지 데이터도 동일하게 수정

# 초기화된 딕셔너리 생성
ani_dict = {}

# 원하는 횟수만큼 반복하여 딕셔너리에 데이터 추가
for i in range(0, len(array_ani)):
    key = f'{i+1}'  # 'anime_숫자' 형태의 키 생성
    value = array_ani[i]  # 원하는 값 (예: array_anime)을 할당
    ani_dict[key] = value  # 딕셔너리에 추가

print(ani_dict)


array_y2k = np.load('C:\\Users\\82105\\OneDrive\\바탕 화면\\Case\\src\\main\\python\\src\\main\\python\\data\\Y2K.npy')

# 초기화된 딕셔너리 생성
y2k_dict = {}

# 원하는 횟수만큼 반복하여 딕셔너리에 데이터 추가
for i in range(0, len(array_y2k)):
    key = f'{i+1}'  # 'anime_숫자' 형태의 키 생성
    value = array_y2k[i]  # 원하는 값 (예: array_anime)을 할당
    y2k_dict[key] = value  # 딕셔너리에 추가

print(y2k_dict)


array_game = np.load('C:\\Users\\82105\\OneDrive\\바탕 화면\\Case\\src\\main\\python\\src\\main\\python\\data\\Game.npy')


# 초기화된 딕셔너리 생성
game_dict = {}

# 원하는 횟수만큼 반복하여 딕셔너리에 데이터 추가
for i in range(0, len(array_game)):
    key = f'{i}'  # 'anime_숫자' 형태의 키 생성
    value = array_game[i]  # 원하는 값 (예: array_anime)을 할당
    game_dict[key] = value  # 딕셔너리에 추가

print(game_dict)

array_art = np.load('C:\\Users\\82105\\OneDrive\\바탕 화면\\Case\\src\\main\\python\\src\\main\\python\\data\\Art.npy')

# 초기화된 딕셔너리 생성
art_dict = {}

# 원하는 횟수만큼 반복하여 딕셔너리에 데이터 추가
for i in range(0, len(array_art)):
    key = f'{i}'  # 'anime_숫자' 형태의 키 생성
    value = array_art[i]  # 원하는 값 (예: array_anime)을 할당
    art_dict[key] = value  # 딕셔너리에 추가

print(art_dict)


array_fashion = np.load('C:\\Users\\82105\\OneDrive\\바탕 화면\\Case\\src\\main\\python\\src\\main\\python\\data\\Fashion.npy')

# 초기화된 딕셔너리 생성
fashion_dict = {}

# 원하는 횟수만큼 반복하여 딕셔너리에 데이터 추가
for i in range(0, len(array_fashion)):
    key = f'{i+1}'  # 'anime_숫자' 형태의 키 생성
    value = array_fashion[i]  # 원하는 값 (예: array_anime)을 할당
    fashion_dict[key] = value  # 딕셔너리에 추가

print(fashion_dict)

array_modern = np.load('C:\\Users\\82105\\OneDrive\\바탕 화면\\Case\\src\\main\\python\\src\\main\\python\\data\\Modern.npy')

# 초기화된 딕셔너리 생성
modern_dict = {}

# 원하는 횟수만큼 반복하여 딕셔너리에 데이터 추가
for i in range(0, len(array_modern)):
    key = f'{i+1}'  # 'anime_숫자' 형태의 키 생성
    value = array_modern[i]  # 원하는 값 (예: array_anime)을 할당
    modern_dict[key] = value  # 딕셔너리에 추가

print(modern_dict)

array_graphic = np.load('C:\\Users\\82105\\OneDrive\\바탕 화면\\Case\\src\\main\\python\\src\\main\\python\\data\\Graphic.npy')

# 초기화된 딕셔너리 생성
graphic_dict = {}

# 원하는 횟수만큼 반복하여 딕셔너리에 데이터 추가
for i in range(0, len(array_graphic)):
    key = f'{i+1}'  # 'anime_숫자' 형태의 키 생성
    value = array_graphic[i]  # 원하는 값 (예: array_anime)을 할당
    graphic_dict[key] = value  # 딕셔너리에 추가


@app.route('/casebycase/selectedImages', methods=['POST'])
def submit_selected_images():


    # 폼에서 전송된 데이터 받기
    input_str = request.form.get('img_num', '')

    # 입력된 문자열을 공백이나 쉼표로 분리하여 리스트로 만들기
    selected_images = input_str.replace(',', ' ').split()

    # 폼에서 선택된 카테고리 값 가져오기
    fileName = request.form.get('selectedCategory', '')

    # fileName = request.json.get('selectedCategoryValue')

    if fileName == '애니메이션':
        category_dict = ani_dict
    elif fileName == '아티스틱':
        category_dict = art_dict
    elif fileName == '패션':
        category_dict = fashion_dict
    elif fileName == 'Y2K':
        category_dict = y2k_dict
    elif fileName == '모던':
        category_dict = modern_dict
    elif fileName == '게임':
        category_dict = game_dict
    elif fileName == '그래픽':
        category_dict = graphic_dict

    # data 딕셔너리에서 key 값이 이미지 리스트의 각 원소와 일치하는 쌍 추출
    data = {key: value for key, value in category_dict.items()}
    print(data)
    result = {key: value for key, value in category_dict.items() if key in selected_images}


    sim_3 = {}
    # 상위 30개 유사도 값을 저장할 리스트
    sorted_top_30 = []

    # 각 쿼리에 대한 유사도 계산
    for query_name, query_embedding in result.items():
        similarities = {filename: distance.cosine(embedding, query_embedding) for filename, embedding in data.items()}
        sim_3[query_name] = similarities

    # 각 쿼리에 대해 반복하여 처리
    for i, (query_name, query_value) in enumerate(sim_3.items(), start=1):
        # 결과 출력
        print(f"{i}번째 쿼리 이미지({query_name})에 대한 유사도 값:")
        print(query_value)

        # 유사도 값 정렬
        sorted_query_value = sorted(query_value.items(), key=lambda x: x[1])

        # 상위 10개 추출
        sorted_top_10 = sorted_query_value[:11]

        # 결과 출력
        print(f"Top 10 유사도 값:")
        print(sorted_top_10)
        sorted_top_30.extend(sorted_top_10)

    # 최종적으로 상위 30개를 출력
    print("최종 Top 30 유사도 값:")
    print(sorted_top_30)




    return render_template('session1.html')


if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0", port=8081)