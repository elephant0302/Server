# 🔜 JRGB
**생성형 AI와 3d 프린터를 활용한 사용자 맞춤 3D 악세사리 출력 서비스** <br>
얼굴을 인식하여 닮은 동물을 알려주고 그에 맞는 3D 악세서리를 출력할 수 있습니다!
<br>

# 👨🏻‍💻 Contributors
|  <div align = center>조현태 </div> | <div align = center> 이준수 </div>                                                                                                                                                                                                                                                                                                                            |
|:----------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|<div align = center> <img src = "https://oopy.lazyrockets.com/api/v2/notion/image?src=https%3A%2F%2Fnoticon-static.tammolo.com%2Fdgggcrkxq%2Fimage%2Fupload%2Fv1567128822%2Fnoticon%2Fosiivsvhnu4nt8doquo0.png&blockId=865f4b2a-5198-49e8-a173-0f893a4fed45&width=256" width = "17" height = "17"/> [hyuntae99](https://github.com/hyuntae99) </div> | <div align = center> <img src = "https://oopy.lazyrockets.com/api/v2/notion/image?src=https%3A%2F%2Fnoticon-static.tammolo.com%2Fdgggcrkxq%2Fimage%2Fupload%2Fv1567128822%2Fnoticon%2Fosiivsvhnu4nt8doquo0.png&blockId=865f4b2a-5198-49e8-a173-0f893a4fed45&width=256" width = "17" height = "17"/> [elephant0302](https://github.com/elephant0302) </div> |
<br>

## 📖 Development Tech
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white">
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white">
<img src="https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white">
<br>

# 💼 Server Architecture
<img src="https://velog.velcdn.com/images/jmjmjmz732002/post/a6c7a7be-ff27-4723-bfe2-d458ed641fab/image.png">
<br>

# 🎢 work flow
<img src="https://velog.velcdn.com/images/hyuntae99/post/61d5f3a7-1146-49d5-8911-3187fc7f3fa1/image.png">

# 🗂️ Directory
```
├── java
│   └── com
│       └── hyunn
│           └── capstone
│               ├── CapstoneApplication
│               ├── config
│               │   ├── AmazonS3Config
│               │   ├── SwaggerConfig
│               │   └── WebConfig
│               ├── controller
│               │   ├── test
│               │   │   ├── ImageDto
│               │   │   ├── PaymentDto
│               │   │   └── TestController
│               │   ├── ImageController
│               │   ├── KakaoLoginController
│               │   ├── KakaoPayController
│               │   ├── MessageController
│               │   ├── OpenAIController
│               │   ├── PrinterController
│               │   └── UserController
│               ├── dto
│               │   ├── Request
│               │   │   ├── ImageToTextRequest
│               │   │   ├── KakaoPayCancelRequest
│               │   │   ├── KakaoPayReadyRequest
│               │   │   ├── MessageRequest
│               │   │   ├── ThreeDimensionRequest
│               │   │   └── UserRequest
│               │   └── Response
│               │   │   ├── ApiStandardResponse
│               │   │   ├── ErrorResponse
│               │   │   ├── ImageToTextResponse
│               │   │   ├── KakaoPayApproveResponse
│               │   │   ├── KakaoPayReadyResponse
│               │   │   ├── MeshyAPIResponse
│               │   │   ├── MessageRespnose
│               │   │   ├── PaymentResponse
│               │   │   ├── ThreeDimesionResponse
│               │   │   └── UserResponse
│               ├── entity
│               │   │── BaseEntity
│               │   │── Description
│               │   │── Image
│               │   │── Payment
│               │   └── User
│               ├── exception
│               │   └── Handler
│               │   │   ├── GlobalExceptionHandler
│               │   │   ├── ImageExceptionHandler
│               │   │   ├── KakaoLoginExceptionHandler
│               │   │   ├── KakaoPayExceptionHandler
│               │   │   ├── MessageExceptionHandler
│               │   │   ├── PrinterServerExceptionHandler
│               │   │   └── UserExceptionHandler
│               │   │── AlreadyRefundedException
│               │   │── ApiKeyNotValidException
│               │   │── ApiNotFoundException
│               │   │── DescriptionNoFoundException
│               │   │── ErrorStatus
│               │   │── FileNotAllowedException
│               │   │── ImageNotFoundException
│               │   │── PaymentNoFoundException
│               │   │── RootUserException
│               │   │── S3UploadException
│               │   │── UnauthorizedImageAccessException
│               │   └── UserNotFoundException
│               ├── repository
│               │   │── DescriptionJpaRespository
│               │   │── ImageJpaRespository
│               │   │── PaymentJpaRespository
│               │   └── UserJpaRepositoty
│               └── service
│                   ├── ImageService
│                   ├── KakaoLoginService
│                   ├── KakaoPayService
│                   ├── MeshyApiService
│                   ├── MessageService
│                   ├── OpenAIService
│                   ├── PrinterService
│                   └── UserService
└── resources
    ├── config 
    │   └── application-local.yml
    ├── templates
    │   ├── KakaoLoginAndPay.html
    │   ├── PaymentFail.html
    │   └── PaymentSuccess.html
    └── application.yml
```

# 📝 Service

**동영상을 GIF로 변환하는 과정에서 부득이하게 화질이 낮아진 점 먼저 양해 구합니다.**

## 1. 얼굴 인식을 통한 닮은 동물 반환 (메인 페이지)

원할한 사용자 경험을 위해서 로그인 없이 사용 가능하도록 만들었습니다. <br>
이미지를 받아 AWS 내부의 flask 서버의 모델로 보내고 얼굴을 인식하여 닮은 동물을 반환합니다. <br>
강아지, 고양이, 토끼, 여우, 사슴, 곰 중 하나로 반환되며 성별과 표정에 따라 결과를 반환합니다.

<img src= "https://velog.velcdn.com/images/hyuntae99/post/4ebf6821-25d0-4c0c-a61a-2cc96e08f084/image.gif">
<br><br>

## 2. 가장 많은 닮은 동물을 3D 악세서리로 변환

앞에서 반환했던 동물 중 가장 닮은 동물을 Meshy AI를 사용하여 3D 악세서리화 합니다. <br>
1 ~ 2분 정도 소요되며 반환된 3D 악세서리는 웹 상에서 확대, 회전 등 여러 조작이 가능합니다. <br>
공유하기 버튼을 통해 공유가 가능하며 다시하기 버튼을 누르면 메인 페이지로 이동합니다.

<img src= "https://velog.velcdn.com/images/hyuntae99/post/ba9aee4a-eb2e-4ae6-ad35-e2164d973fde/image.gif">
<img src= "https://velog.velcdn.com/images/hyuntae99/post/d4ed14f7-b45e-456e-86e4-c9ae1e017d7c/image.gif">

<br><br>

## 3-1. 3D 악세서리 출력을 위한 결제 

3D 악세서리가 마음에 들었다면 출력해서 배송할 수 있는 서비스를 제공하고 있습니다.<br>
출력하기 버튼을 누르면 카카오 로그인과 카카오 페이로 출력/배송 비용을 결제합니다. <br>
<ins>해당 서비스는 임시 서비스로 실제로 결제가 되지 않습니다.</ins>

<img src= "https://velog.velcdn.com/images/hyuntae99/post/22b0ec7c-404e-4f51-b612-6c85a20ccbfe/image.gif">
<br><br>

## 3-2. 3D 악세서리 출력하기 (문자 및 이메일 알림 서비스)

결제가 완료되면 3D 프린터 서버로 요청을 보내고 해당 3D 악세서리를 출력합니다. <br>
출력이 완료되면 문자와 이메일로 출력이 완료되었다는 것을 알려줍니다. <br>
<ins>해당 서비스는 임시 서비스로 실제로 출력이 되지 않습니다.</ins>

<img src= "https://velog.velcdn.com/images/hyuntae99/post/099ba22c-46aa-42bf-bd71-cef3c29f0a48/image.gif">

<img src= "https://velog.velcdn.com/images/hyuntae99/post/ad2bd783-74e6-4438-a837-f01761a8fc67/image.png" width="550" height="400"> <img src= "https://velog.velcdn.com/images/hyuntae99/post/7aaf3d2e-5b19-4db7-ae63-7669213f4993/image.jpg" width="400" height="250">
<br><br>

## 4. 결제 내역 확인
결제 내역을 확인할 수 있으며 이용했던 사진과 3D 악세서리를 다운받을 수 있습니다.

<img src= "https://velog.velcdn.com/images/hyuntae99/post/95b0669e-7b80-4e10-85bf-5dd44fe08e54/image.gif">
<br><br>

### 더 많은 정보는 [발표영상](https://youtu.be/ZWVOgQ6WW7c?si=fDE5nHkoOVykHuGc)에서 확인하실 수 있습니다.
<br>

## + 논문 등록
<img src = "https://velog.velcdn.com/images/hyuntae99/post/dd0701c6-4f26-4ccb-8bda-fd2812c936f6/image.png">
<img src = "https://velog.velcdn.com/images/hyuntae99/post/6434f478-c70c-47f0-8198-78d1fb4781bf/image.png">

### 더 자세한 정보는 [여기서](https://drive.google.com/file/d/1A5cWRG36dFshzS6_ID3MLHSCMyM3K1WN/view?usp=sharing) 다운 받아 확인하실 수 있습니다. <br><br> 해당 내용은 139.p에 기재되어있습니다.
