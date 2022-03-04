# Clone_Chat
## 회원가입, 로그인 화면

![KakaoTalk_20220304_094211863](https://user-images.githubusercontent.com/77681440/156677628-81314361-9060-40c5-9442-11d0d5943d7a.jpg)
![KakaoTalk_20220304_094211863_01](https://user-images.githubusercontent.com/77681440/156677630-bf81086c-de42-4cf3-ab89-e373239af87a.jpg)
![KakaoTalk_20220304_094211863_02](https://user-images.githubusercontent.com/77681440/156677616-7c1f6590-be09-49a9-bc10-1f7af762e886.jpg)

> Firebase에서 제공하는 회원가입, 로그인 함수를 이용해서 회원가입 시 사용자가 Firebase Authentication에 저장되고, Firebase Database에 사용자의 Email 정보를 저장한다.
> 
> 채팅을 이용하면 사용자의 고유 FCM Token을 저장한다.
> 
> 사용자의 정보는 Firebase Database의 컬렉션 'profile' 내 사용자의 Email을 이름으로 하는 문서에 저장된다.

## 사용자 화면

![KakaoTalk_20220304_094211863_03](https://user-images.githubusercontent.com/77681440/156677620-d3df6783-2090-451f-b49f-38ea78057718.jpg)
![KakaoTalk_20220304_094300142](https://user-images.githubusercontent.com/77681440/156677625-2314049e-5e97-42a9-bd80-e0038d8d4670.jpg)
![KakaoTalk_20220304_094300142_01](https://user-images.githubusercontent.com/77681440/156677626-915804d1-0f10-4c5c-b8ea-02425a77435c.jpg)

화면 구성
> 사용자 화면에서는 사용자 본인과 앱을 사용하는(Firebase Database 'profile' 컬렉션에 이메일이 파일 이름으로 등록되어 있는) 다른 사용자들의 프로필 정보가 나타난다.
>
> 사용자 화면에서는 사용자가 지정한 이름/상태메세지/사진/프로필음악 을 볼 수 있고, 항목 클릭 시 사용자의 프로필 상세 화면으로 이동한다.
> 
> 사용자 프로필 상세 화면에서는 해당 사용자가 설정한 프로필사진/배경사진/이름/상태메세지/프로필음악 을 볼 수 있고, 해당 사용자와의 채팅방을 생성해 채팅을 진행할 수 있다.
> 
> 본인의 프로필 상세 화면에서는 '나와의 채팅방'을 생성할 수 있고, 프로필사진/배경사진/이름/상태메세지/프로필음악 를 변경할 수 있다.

화면 데이터
> 이름/상태메세지/프로필음악 의 변경사항은 FireBase Database 사용자 문서에 저장된다.
> 
> 프로필사진/배경사진 은 Firebase Storage의 사용자 Email을 이름으로 하는 파일 안에 저장되고, 변경 시 기존 데이터를 덮어씌운다.


## 채팅 화면

-채팅방 리스트

![KakaoTalk_20220304_094211863_04](https://user-images.githubusercontent.com/77681440/156677621-3da6ce95-7329-482f-b942-7f9570c4548c.jpg)

* 사용자가 해당되어 있는 채팅 리스트들을 파이어베이스에서 받아 와, 나열 되어 있다.
  읽지 않은 메세지가 있다면, 우측에 빨간색으로 메세지 수가 띄워진다.


-채팅방

![KakaoTalk_20220304_094211863_05](https://user-images.githubusercontent.com/77681440/156677623-57ab187f-1bf5-479f-9d7f-6a4e0b81ede9.jpg)

* 메세지를 보내면 파이어베이스 실시간 데이터베이스에 정보들이 저장된다.
- 처음 메세지를 보낸 경우 chatRoomUser가 새로 생성
- chatRoomUser의 chatRoomId를 받아와, Messages와 UserRoom 생성

* 채팅방에 들어갈/나갈 경우, UserStatus가 변경된다. 
- 메세지가 왔을/보냈을 경우, 상대방의 UserStatus가 "IN"인 상태라면, 읽음 표시를 해준다.

* 파이어베이스 실시간 데이터베이스 구조

Messages  (메세지 정보)
|----ChatRoomId  (채팅방 아이디)
|   |----MessageId   (각 메세지의 아이디(random))
|      |----message   (메세지 내용) - String
|      |----read      (읽음 표시) - Boolean
|      |----sender    (보낸 사람) - String
|      |----timestamp (보낸 시간) - Long


UserRoom  (마지막 메세지 정보)
|----ChatRoomId  (채팅방 아이디)
|   |----lastmessage   (마지막 메세지 내용) - String
|   |----sender    (보낸 사람) - String
|   |----timestamp (보낸 시간) - Long


UserStatus  (사용자 채팅방 접속 정보)
|----ChatRoomId  (채팅방 아이디)
|   |----user1 (사용자 접속 상태) - String
|   |----user2 (사용자 접속 상태) - String


chatRoomUser  (생성된 채팅방 정보)
|----ChatRoomId  (채팅방 아이디)
|   |----user1   (사용자 이메일) - String
|   |----user2   (사용자 이메일) - String


## 보완할 점
1. 비동기 중복 Callback 해결
2. Local DB 사용을 통해 채팅 데이터를 Firebase에 저장하지 않기
3. 클린 아키텍쳐 - MVVM 패턴 사용
4. 1대다 채팅 구조를 위한 데이터 구조 수정
5. fcm messaging의 token 보안
6. RecyclerView notifyDataSetChanged() 사용 자제
7. 버튼 중복클릭 방지
8. 다크모드 등을 고려한 디자인으로 수정
9. xml value 적극 활용


