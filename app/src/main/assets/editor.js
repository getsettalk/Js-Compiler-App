// Simple CodeMirror-like behavior for textarea if real CodeMirror is not available
// In a production app, you'd include the real codemirror.js and codemirror.css here.

const editor = document.getElementById('editor');

function setCode(code) {
    editor.value = code;
}

function getCode() {
    return editor.value;
}

// Basic tab support
editor.addEventListener('keydown', function(e) {
    if (e.key == 'Tab') {
        e.preventDefault();
        var start = this.selectionStart;
        var end = this.selectionEnd;
        this.value = this.value.substring(0, start) + "    " + this.value.substring(end);
        this.selectionStart = this.selectionEnd = start + 4;
    }
});
