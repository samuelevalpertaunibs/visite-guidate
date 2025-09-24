const express = require('express');
const app = express();
const port = 3000;

// Data iniziale
let currentDate = new Date();

// Middleware per servire il frontend
app.use(express.static('public'));

// Rotta per ottenere la data corrente
app.get('/api/datetime', (req, res) => {
    res.type('text/plain').send(currentDate.toISOString().slice(0, 10)); // "YYYY-MM-DD"
});

// Rotta per modificare la data
app.post('/api/modifyDate', (req, res) => {
    const action = req.query.action;

    if (action === 'addDay') {
        currentDate.setDate(currentDate.getDate() + 1);
    } else if (action === 'subtractDay') {
        currentDate.setDate(currentDate.getDate() - 1);
    } else if (action === 'addWeek') {
        currentDate.setDate(currentDate.getDate() + 7);
    } else if (action === 'subtractWeek') {
        currentDate.setDate(currentDate.getDate() - 7);
    }

    res.json({ date: currentDate.toISOString().slice(0, 10) }); // Solo la data
});

// Rotta per resettare la data
app.post('/api/reset', (req, res) => {
    currentDate = new Date();
    res.json({ date: currentDate.toISOString().slice(0, 10) }); // Solo la data
});

// Incremento della data ogni ora
setInterval(() => {
    currentDate.setDate(currentDate.getDate() + 1);
}, 3600000); // Aggiorna ogni ora

// Avvia il server
app.listen(port, () => {
    console.log(`Server in ascolto sulla porta ${port}`);
});
