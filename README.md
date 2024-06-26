# 🔜 JRGB
**생성형 AI와 3d print를 활용한 사용자 맞춤 3D 악세사리 출력 서비스**
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
<img src="https://velog.velcdn.com/images/hyuntae99/post/21d1a9c3-63b4-4ac8-95df-74c475b3d372/image.png">

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
│               │   │   ├── ImageRequest
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
└── test
```

# 📝 Service

추후 인터페이스와 기능 소개 작성...
<br>
참고자료 : [Swagger API 문서](https://capstone.hyunn.site/swagger-ui/index.html)