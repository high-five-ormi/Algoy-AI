let currentEventSource = null;

function escapeHtml(unsafe) {
  return unsafe
  .replace(/&/g, "&amp;")
  .replace(/</g, "&lt;")
  .replace(/>/g, "&gt;")
  .replace(/"/g, "&quot;")
  .replace(/'/g, "&#039;");
}

function sendMessage() {
  var input = document.getElementById('user-input');
  var message = input.value;
  if (!message.trim()) return;
  input.value = '';

  if (currentEventSource) {
    currentEventSource.close();
  }

  var escapedMessage = escapeHtml(message);
  $('#messages').append('<p><strong>You:</strong> ' + escapedMessage + '</p>');
  var aiResponseElement = $('<p><strong>AI:</strong> <span class="loading">Thinking...</span></p>').appendTo('#messages');

  $('#send-button').prop('disabled', true);

  currentEventSource = new EventSource('/ai/api/chat/stream?content=' + encodeURIComponent(message));
  var lastResponse = '';

  currentEventSource.onmessage = function(event) {
    try {
      var jsonResponse = JSON.parse(event.data);
      if (jsonResponse && jsonResponse.data && jsonResponse.data.content) {
        lastResponse = jsonResponse.data.content;
        var parsedMarkdown = marked.parse(lastResponse);

        // 모든 코드 블록을 div로 감싸기
        parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
        parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');

        aiResponseElement.html('<strong>AI:</strong> <div class="markdown-body">' + parsedMarkdown + '</div>');

        // 코드 블록에 구문 강조 적용
        aiResponseElement.find('pre code').each(function(i, block) {
          hljs.highlightBlock(block);
        });
      }
    } catch (error) {
      console.error('Error processing response:', error, 'Raw data:', event.data);
    }
  };

  currentEventSource.onerror = function(event) {
    console.error('EventSource failed:', event);
    currentEventSource.close();
    $('#send-button').prop('disabled', false);
    if (lastResponse) {
      var parsedMarkdown = marked.parse(lastResponse);
      // 모든 코드 블록을 div로 감싸기
      parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
      parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
      aiResponseElement.html('<strong>AI:</strong> <div class="markdown-body">' + parsedMarkdown + '</div>');
    } else {
      aiResponseElement.find('.loading').text('Error occurred. Please try again.');
    }
  };

  currentEventSource.onclose = function(event) {
    $('#send-button').prop('disabled', false);
    if (lastResponse) {
      var parsedMarkdown = marked.parse(lastResponse);
      // 모든 코드 블록을 div로 감싸기
      parsedMarkdown = parsedMarkdown.replace(/<pre><code([^>]*)>/g, '<div class="code-block-wrapper"><pre><code$1>');
      parsedMarkdown = parsedMarkdown.replace(/<\/code><\/pre>/g, '</code></pre></div>');
      aiResponseElement.html('<strong>AI:</strong> <div class="markdown-body">' + parsedMarkdown + '</div>');
    }
  };
}

// Enter 키로 메시지 전송
document.getElementById('user-input').addEventListener('keypress', function(event) {
  if (event.key === 'Enter') {
    sendMessage();
  }
});

// 전송 버튼 클릭 이벤트
document.getElementById('send-button').addEventListener('click', sendMessage);