require('dotenv').config()
const jwt = require('jsonwebtoken');

exports.boothAuth = function(req, res, next) {
    const token = req.headers['access-token'];
    if(token != null) {
        jwt.verify(token, process.env.ACCESS_TOKEN_SECRET, (err, resp) => {
            if(err || resp.user_type != 1) {
                let message = "Invalid credentials";
                if(err.name === 'TokenExpiredError') {
                    message = "Your login has expired, please re-login."
                }
                res.statusCode = 200;
                return res.json({status: 401, message: message});
            }
            req.user_id = resp.user_id;
            req.user_role = resp.user_type;
            next();
        });
    } else {
        res.statusCode = 200;
        return res.json({status: 401, message: "Invalid credentials"});
    }
}

exports.userAuth = function(req, res, next) {
    const token = req.headers['access-token'];
    if(token != null) {
        jwt.verify(token, process.env.ACCESS_TOKEN_SECRET, (err, resp) => {
            if(err || resp.user_type != 2) {
                let message = "Invalid credentials";
                if(err.name === 'TokenExpiredError') {
                    message = "Your login has expired, please re-login."
                }
                res.statusCode = 200;
                return res.json({status: 401, message: message});
            }
            req.user_id = resp.user_id;
            req.user_role = resp.user_type;
            next();
        });
    } else {
        res.statusCode = 200;
        return res.json({status: 401, message: "Invalid credentials"});
    }
}

exports.commonAuth = function(req, res, next) {
    const token = req.headers['access-token'];
    if(token != null) {
        jwt.verify(token, process.env.ACCESS_TOKEN_SECRET, (err, resp) => {
            if(err) {
                let message = "Invalid credentials";
                if(err.name === 'TokenExpiredError') {
                    message = "Your login has expired, please re-login."
                }
                res.statusCode = 200;
                return res.json({status: 401, message: message});
            }
            req.user_id = resp.user_id;
            req.user_role = resp.user_type;
            next();
        });
    } else {
        res.statusCode = 200;
        return res.json({status: 401, message: "Invalid credentials"});
    }
}