let currentEventSource = null;

// Configure marked options
marked.setOptions({
  highlight: function(code, lang) {
    const language = hljs.getLanguage(lang) ? lang : 'plaintext';
    return hljs.highlight(code, { language }).value;
  },
  langPrefix: 'hljs language-'
});

function sendMessage() {
  var input = document.getElementById('user-input');
  var message = input.value;
  if (!message.trim()) return;
  input.value = '';

  // Cancel the previous request if it's still active
  if (currentEventSource) {
    currentEventSource.close();
  }

  $('#messages').append('<p><strong>You:</strong> ' + message + '</p>');
  var aiResponseElement = $('<p><strong>AI:</strong> <span class="loading">Thinking...</span></p>').appendTo('#messages');

  $('#send-button').prop('disabled', true);

  currentEventSource = new EventSource('/ai/api/chat/stream?content=' + encodeURIComponent(message));
  var fullResponse = '';

  currentEventSource.onmessage = function(event) {
    try {
      var jsonResponse = JSON.parse(event.data);
      if (jsonResponse && jsonResponse.response) {
        var responseLines = jsonResponse.response.split('\n');
        var lastValidResponse = '';

        responseLines.forEach(function(line) {
          try {
            var lineData = JSON.parse(line.replace(/'/g, '"'));
            if (lineData.type === 'complete' || lineData.type === 'continue') {
              lastValidResponse += lineData.data.content;
            }
          } catch (lineError) {
            // If parsing fails, assume it's a plain text response
            lastValidResponse += line;
          }
        });

        if (lastValidResponse) {
          fullResponse = lastValidResponse; // Accumulate the response
          // Convert markdown to HTML
          var htmlResponse = marked.parse(fullResponse);
          aiResponseElement.html('<strong>AI:</strong> ' + htmlResponse);
          // Apply syntax highlighting to code blocks
          aiResponseElement.find('pre code').each(function(i, block) {
            hljs.highlightBlock(block);
          });
        }

        if (responseLines[responseLines.length - 1].includes('"type": "complete"') ||
            responseLines[responseLines.length - 1].includes("'type': 'complete'")) {
          currentEventSource.close();
          $('#send-button').prop('disabled', false);
        }
      }
    } catch (error) {
      console.error('Error processing response:', error, 'Raw data:', event.data);
    }
  };

  currentEventSource.onerror = function(event) {
    console.error('EventSource failed:', event);
    currentEventSource.close();
    $('#send-button').prop('disabled', false);
    aiResponseElement.find('.loading').text('Error occurred. Please try again.');
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