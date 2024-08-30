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

  let fullResponse = '';
  let isComplete = false;

  currentEventSource.onmessage = function(event) {
    try {
      var jsonResponse = JSON.parse(event.data);
      if (jsonResponse && jsonResponse.response) {
        var responseLines = jsonResponse.response.split('\n');

        responseLines.forEach(function(line) {
          try {
            var lineData = JSON.parse(line.replace(/'/g, '"'));
            if (lineData.type === 'complete') {
              fullResponse = lineData.data.content;
              isComplete = true;
            }
          } catch (lineError) {
            // If parsing fails, assume it's a plain text response
            console.warn('Failed to parse line:', line);
          }
        });

        if (isComplete) {
          // Only update the response when we have the complete message
          var safeResponse = fullResponse.replace(/</g, '&lt;').replace(/>/g, '&gt;');
          try {
            var htmlResponse = marked.parse(safeResponse);
            aiResponseElement.html('<strong>AI:</strong> ' + htmlResponse);
          } catch (markdownError) {
            // If markdown parsing fails, display the plain text
            aiResponseElement.html('<strong>AI:</strong> ' + safeResponse);
          }

          // Apply syntax highlighting to code blocks
          aiResponseElement.find('pre code').each(function(i, block) {
            hljs.highlightBlock(block);
          });

          currentEventSource.close();
          $('#send-button').prop('disabled', false);
        }
      }
    } catch (error) {
      console.error('Error processing response:', error, 'Raw data:', event.data);
      aiResponseElement.html('<strong>AI:</strong> Error occurred while processing the response. Please try again.');
    }
  };

  currentEventSource.onerror = function(event) {
    console.error('EventSource failed:', event);
    currentEventSource.close();
    $('#send-button').prop('disabled', false);
    aiResponseElement.html('<strong>AI:</strong> Error occurred. Please try again.');
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