const canvas = document.getElementById('mapCanvas');
const ctx = canvas.getContext('2d');

let scale = 1;
let offsetX = 0;
let offsetY = 0;

// Campus buildings data from Java backend
let buildings = [];

// Fetch buildings from Java backend
fetch('/api/buildings')
    .then(response => response.json())
    .then(data => {
        buildings = data;
        drawMap();
    });

function drawMap() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    ctx.save();
    ctx.scale(scale, scale);
    ctx.translate(offsetX, offsetY);
    
    // Draw paths
    ctx.strokeStyle = '#95a5a6';
    ctx.lineWidth = 3;
    ctx.beginPath();
    ctx.moveTo(100, 200);
    ctx.lineTo(500, 200);
    ctx.moveTo(300, 50);
    ctx.lineTo(300, 350);
    ctx.stroke();
    
    // Draw buildings
    buildings.forEach(building => {
        ctx.fillStyle = building.color;
        ctx.fillRect(building.x, building.y, building.width, building.height);
        
        ctx.fillStyle = 'white';
        ctx.font = '12px Arial';
        ctx.textAlign = 'center';
        ctx.fillText(building.name, 
            building.x + building.width/2, 
            building.y + building.height/2 + 4);
    });
    
    ctx.restore();
}

function zoomIn() {
    scale = Math.min(scale * 1.2, 3);
    drawMap();
}

function zoomOut() {
    scale = Math.max(scale / 1.2, 0.5);
    drawMap();
}

function resetView() {
    scale = 1;
    offsetX = 0;
    offsetY = 0;
    drawMap();
}

// Download button functionality with Java backend
document.getElementById('downloadBtn').addEventListener('click', () => {
    fetch('/api/download')
        .then(response => response.json())
        .then(data => {
            alert(`${data.message}\nSize: ${data.size}`);
        });
});

// Mouse interaction for panning
let isDragging = false;
let lastX, lastY;

canvas.addEventListener('mousedown', (e) => {
    isDragging = true;
    lastX = e.offsetX;
    lastY = e.offsetY;
});

canvas.addEventListener('mousemove', (e) => {
    if (isDragging) {
        const deltaX = (e.offsetX - lastX) / scale;
        const deltaY = (e.offsetY - lastY) / scale;
        offsetX += deltaX;
        offsetY += deltaY;
        lastX = e.offsetX;
        lastY = e.offsetY;
        drawMap();
    }
});

canvas.addEventListener('mouseup', () => {
    isDragging = false;
});

// Initial draw
drawMap();