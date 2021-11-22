var createError = require('http-errors');
var express = require('express');
var path = require('path');
var logger = require('morgan');
let cors = require('cors');

var app = express();

app.use(cors());
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));

let routes = require('./routes')(app);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  const error = new Error('Not found');
  error.status = 404;
  next(error)
});

// error handler
app.use(function(err, req, res, next) {
  res.status(err.status || 500)
  res.json({
    error: {
      message: err.message
    }
  });
});

module.exports = app;
