let currentEventSource = null;

function sendMessage() {
  var input = document.getElementById('user-input');
  var message = input.value;
  if (!message.trim()) return;
  input.value = '';

  if (currentEventSource) {
    currentEventSource.close();
  }

  $('#messages').append('<p><strong>You:</strong> ' + message + '</p>');
  var aiResponseElement = $('<p><strong>AI:</strong> <span class="loading">Thinking...</span></p>').appendTo('#messages');

  $('#send-button').prop('disabled', true);

  currentEventSource = new EventSource('/ai/api/chat/stream?content=' + encodeURIComponent(message));
  var lastResponse = '';

  currentEventSource.onmessage = function(event) {
    try {
      console.log('Raw event data:', event.data);  // For debugging
      var jsonResponse = JSON.parse(event.data);
      if (jsonResponse && jsonResponse.data && jsonResponse.data.content) {
        lastResponse = jsonResponse.data.content;
        var parsedMarkdown = marked.parse(lastResponse);
        aiResponseElement.html('<strong>AI:</strong> <div class="markdown-body">' + parsedMarkdown + '</div>');

        // Apply syntax highlighting to code blocks
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
      aiResponseElement.html('<strong>AI:</strong> <div class="markdown-body">' + parsedMarkdown + '</div>');
    } else {
      aiResponseElement.find('.loading').text('Error occurred. Please try again.');
    }
  };

  currentEventSource.onclose = function(event) {
    $('#send-button').prop('disabled', false);
    if (lastResponse) {
      var parsedMarkdown = marked.parse(lastResponse);
      aiResponseElement.html('<strong>AI:</strong> <div class="markdown-body">' + parsedMarkdown + '</div>');
    }
  };
}

// Allow sending message with Enter key
document.getElementById('user-input').addEventListener('keypress', function(event) {
  if (event.key === 'Enter') {
    sendMessage();
  }
});

// Attach click event to send button
document.getElementById('send-button').addEventListener('click', sendMessage);