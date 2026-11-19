const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const mysql = require('mysql2/promise');

const app = express();
app.use(cors());
app.use(bodyParser.json());

// Configure MySQL connection (adjust credentials)
const pool = mysql.createPool({
  host: 'localhost',
  user: 'root',
  password: '', // add your password
  database: 'vet_san_marcos',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0,
});

// Helper to handle async errors
const asyncHandler = fn => (req, res, next) => {
  Promise.resolve(fn(req, res, next)).catch(next);
};

// ---- Product CRUD Endpoints ----
// Assuming a table "producto" exists. If not, create it:
// CREATE TABLE producto (id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(100), precio DECIMAL(10,2), stock INT, descripcion TEXT, activo TINYINT(1) DEFAULT 1);

// GET all products
app.get('/api/productos', asyncHandler(async (req, res) => {
  const [rows] = await pool.query('SELECT * FROM producto WHERE activo = 1');
  res.json(rows);
}));

// GET product by id
app.get('/api/productos/:id', asyncHandler(async (req, res) => {
  const [rows] = await pool.query('SELECT * FROM producto WHERE id = ? AND activo = 1', [req.params.id]);
  if (rows.length === 0) return res.status(404).json({message: 'Producto no encontrado'});
  res.json(rows[0]);
}));

// CREATE new product
app.post('/api/productos', asyncHandler(async (req, res) => {
  const {nombre, precio, stock, descripcion} = req.body;
  const [result] = await pool.execute(
    'INSERT INTO producto (nombre, precio, stock, descripcion) VALUES (?,?,?,?)',
    [nombre, precio, stock, descripcion]
  );
  const insertedId = result.insertId;
  const [rows] = await pool.query('SELECT * FROM producto WHERE id = ?', [insertedId]);
  res.status(201).json(rows[0]);
}));

// UPDATE existing product
app.put('/api/productos/:id', asyncHandler(async (req, res) => {
  const {nombre, precio, stock, descripcion, activo} = req.body;
  const fields = [];
  const values = [];
  if (nombre !== undefined) {fields.push('nombre = ?'); values.push(nombre);} 
  if (precio !== undefined) {fields.push('precio = ?'); values.push(precio);} 
  if (stock !== undefined) {fields.push('stock = ?'); values.push(stock);} 
  if (descripcion !== undefined) {fields.push('descripcion = ?'); values.push(descripcion);} 
  if (activo !== undefined) {fields.push('activo = ?'); values.push(activo);} 
  if (fields.length === 0) return res.status(400).json({message: 'No se enviaron campos para actualizar'});

  values.push(req.params.id);
  const sql = `UPDATE producto SET ${fields.join(', ')} WHERE id = ?`;
  await pool.execute(sql, values);
  const [rows] = await pool.query('SELECT * FROM producto WHERE id = ?', [req.params.id]);
  res.json(rows[0]);
}));

// DELETE (soft‑delete) product
app.delete('/api/productos/:id', asyncHandler(async (req, res) => {
  await pool.execute('UPDATE producto SET activo = 0 WHERE id = ?', [req.params.id]);
  res.status(204).send();
}));

// Global error handler
app.use((err, req, res, next) => {
  console.error(err);
  res.status(500).json({message: 'Error interno del servidor'});
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Servidor escuchando en puerto ${PORT}`));
