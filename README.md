# Clone_Chat
## 회원가입, 로그인 화면

![KakaoTalk_20220304_094211863](https://user-images.githubusercontent.com/77681440/156677628-81314361-9060-40c5-9442-11d0d5943d7a.jpg)
![KakaoTalk_20220304_094211863_01](https://user-images.githubusercontent.com/77681440/156677630-bf81086c-de42-4cf3-ab89-e373239af87a.jpg)
![KakaoTalk_20220304_094211863_02](https://user-images.githubusercontent.com/77681440/156677616-7c1f6590-be09-49a9-bc10-1f7af762e886.jpg)

> Firebase에서 제공하는 회원가입, 로그인 함수를 이용해서 Firebase Database에 사용자의 Email 정보를 저장하고,
> 
> 채팅을 이용하면 사용자의 FCM Token을 저장한다.
> 
> 사용자의 정보는 Firebase Database의 컬렉션 'profile' 내 사용자의 Email을 이름으로 하는 파일에 저장된다.

## 사용자 화면

![KakaoTalk_20220304_094211863_03](https://user-images.githubusercontent.com/77681440/156677620-d3df6783-2090-451f-b49f-38ea78057718.jpg)
![KakaoTalk_20220304_094300142](https://user-images.githubusercontent.com/77681440/156677625-2314049e-5e97-42a9-bd80-e0038d8d4670.jpg)
![KakaoTalk_20220304_094300142_01](https://user-images.githubusercontent.com/77681440/156677626-915804d1-0f10-4c5c-b8ea-02425a77435c.jpg)

> 사용자 화면에서는 사용자 본인과 앱을 사용하는(Firebase Database 'profile' 컬렉션에 이메일이 파일 이름으로 등록되어 있는) 다른 사용자들의 프로필 정보가 나타난다.
>
> 사용자 화면에서는 사용자가 지정한 이름/상태메세지/사진 을 볼 수 있고, 항목 클릭 시 사용자의 프로필 상세 화면으로 이동한다.
> 
> 사용자 프로필 상세 화면에서는 해당 사용자가 설정한 프로필사진/배경사진/이름/상태메세지 를 볼 수 있고, 해당 사용자와의 채팅방을 생성해 채팅을 진행할 수 있다.
> 
> 본인의 프로필 상세 화면에서는 '나와의 채팅방'을 생성할 수 있고, 프로필사진/배경사진/이름/상태메세지 를 변경할 수 있다.




## 채팅 화면

![KakaoTalk_20220304_094211863_04](https://user-images.githubusercontent.com/77681440/156677621-3da6ce95-7329-482f-b942-7f9570c4548c.jpg)
![KakaoTalk_20220304_094211863_05](https://user-images.githubusercontent.com/77681440/156677623-57ab187f-1bf5-479f-9d7f-6a4e0b81ede9.jpg)


