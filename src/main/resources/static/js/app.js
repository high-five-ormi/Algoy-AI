class ChatApp {
  constructor() {
    this.stompClient = null;
    this.init();
  }

  // 초기화 메서드
  init() {
    this.connect(); // WebSocket 연결 설정
    document.getElementById('sendButton').addEventListener('click', () => this.sendMessage()); // 전송 버튼 클릭 이벤트 설정
  }

  // WebSocket 연결 메서드
  connect() {
    const socket = new SockJS('/chat-websocket', null, {transports: ["websocket", "xhr-streaming", "xhr-polling"]}); // SockJS를 사용하여 WebSocket 연결 생성
    this.stompClient = Stomp.over(socket); // STOMP 클라이언트 생성
    this.stompClient.connect({}, (frame) => {
      console.log('Connected: ' + frame); // 연결 성공 시 콘솔에 프레임 출력
      this.stompClient.subscribe('/topic/messages', (messageOutput) => {
        this.showMessage(JSON.parse(messageOutput.body)); // 메시지 수신 시 화면에 출력
      });
    });
  }

  // 메시지 전송 메서드
  sendMessage() {
    const username = document.getElementById('username').value.trim(); // 사용자 이름 입력 필드 값 가져오기 (공백 제거)
    const message = document.getElementById('message').value.trim(); // 메시지 입력 필드 값 가져오기(공백 제거)
    if (username && message) {
      this.stompClient.send("/app/chat", {}, JSON.stringify({'username': username, 'content': message})); // STOMP를 통해 메시지 전송
      document.getElementById('message').value = '';  // 메시지 전송 후 입력 필드 초기화
    } else {
      alert("Username and message must not be empty!"); // 사용자 이름과 메시지가 비어있으면 경고 메시지 출력
    }
  }

  // 메시지 출력 메서드
  showMessage(message) {
    const messages = document.getElementById('messages'); // 메시지 출력 영역 가져오기
    const messageElement = document.createElement('div'); // 새로운 메시지 요소 생성
    messageElement.textContent = `${message.username}: ${message.content} (${message.createdAt})`; // 메시지 내용 설정
    messages.appendChild(messageElement); // 메시지 출력 영역에 메시지 요소 추가
    messages.scrollTop = messages.scrollHeight;  // 스크롤을 최신 메시지로 이동
  }
}

// DOMContentLoaded 이벤트가 발생하면 ChatApp 인스턴스 생성
document.addEventListener('DOMContentLoaded', () => {
  new ChatApp();
});