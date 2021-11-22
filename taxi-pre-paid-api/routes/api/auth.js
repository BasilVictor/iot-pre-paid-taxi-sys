require('dotenv').config()
let router = require('express').Router();
const bodyParser = require('body-parser');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');
const config = require('../config');
let connection = require('mysql2').createPool(config.database);

router.use(bodyParser.json());

router.post('/login', (req, res, next) => {
    const request = req.body;
    if(request.user_id != null && request.password != null) {
        connection.getConnection((error, tempConnection) => {
            if(error) {
                console.log(error);
                res.statusCode = 200;
                return res.json({status: -1, message: "Something went wrong"});
            }
            let sqlQuery = `SELECT * FROM users WHERE user_id = ?;`
            let fieldsArray = [request.user_id];
            tempConnection.query(sqlQuery, fieldsArray, async (error, results, fields) => {
                tempConnection.release();
                if(error) {
                    console.log(error);
                    return res.status(200).json({status: -1, message: "Something went wrong"});
                }
                if(results.length == 1) {
                    const user = results[0];
                    const storedPassword = user.user_password;
                    const validPassword = await bcrypt.compare(request.password, storedPassword);
                    if(validPassword) {
                        const payload = { user_id: user.user_id, user_type: user.user_type };
                        const accessToken = jwt.sign(payload, process.env.ACCESS_TOKEN_SECRET);
                        return res.status(200).json({ status: 1, data: { "access-token": accessToken, user_type: user.user_type, address: user.user_address }, message: "Signed in successfully"});
                    } else {
                        return res.status(200).json({status: -1, message: "Invalid credentials"});
                    }
                } else {
                    res.status(200).json({ status: -1, message: "Invalid credentials" });
                }
            });
        });
    } else {
        return res.status(200).json({status: -1, message: "Invalid request"});
    }
});

// DEPRECATED
router.post('/user/login', (req, res, next) => {
    const request = req.body;
    if(request.vehicle_id != null && request.password != null) {
        connection.getConnection((error, tempConnection) => {
            if(error) {
                console.log(error);
                res.statusCode = 200;
                return res.json({status: -1, message: "Something went wrong"});
            }
            let sqlQuery = `SELECT * FROM users WHERE vehicle_id = ?;`
            let fieldsArray = [request.vehicle_id];
            tempConnection.query(sqlQuery, fieldsArray, async (error, results, fields) => {
                tempConnection.release();
                if(error) {
                    console.log(error);
                    return res.status(200).json({status: -1, message: "Something went wrong"});
                }
                if(results.length == 1) {
                    const user = results[0];
                    const storedPassword = user.user_password;
                    const validPassword = await bcrypt.compare(request.password, storedPassword);
                    if(validPassword) {
                        const payload = { user_id: user.vehicle_id, user_type: 2 };
                        const accessToken = jwt.sign(payload, process.env.ACCESS_TOKEN_SECRET);
                        return res.status(200).json({ status: 1, "access-token": accessToken, user_type: 2, message: "Signed in successfully"});
                    } else {
                        return res.status(200).json({status: -1, message: "Invalid credentials"});
                    }
                } else {
                    res.status(200).json({ success: 1, message: "Invalid credentials" });
                }
            });
        });
    } else {
        return res.status(200).json({status: -1, message: "Invalid request"});
    }
});

router.post('/user/signup', async (req, res, next) => {
    const request = req.body;
    if(request.uid != null && request.vehicle_id != null && request.password != null && request.name != null && request.mobile != null) {
        try {
            const salt = await bcrypt.genSalt(10);
            const hashedPassword = await bcrypt.hash(request.password, salt);
            connection.getConnection((error, tempConnection) => {
                if(error) {
                    console.log(error);
                    res.statusCode = 200;
                    return res.status(200).json({status: -1, message: "Something went wrong"});
                }
                let sqlQuery = `INSERT INTO users (uid, vehicle_id, user_name, user_password, mobile) VALUES (?,?,?,?,?);`;
                let fieldsArray = [request.uid, request.vehicle_id, request.name, hashedPassword, request.mobile];
                tempConnection.query(sqlQuery, fieldsArray, (error, results, fields) => {
                    tempConnection.release();
                    if(error) {
                        console.log(error);
                        if(error.code == "ER_DUP_ENTRY") {
                            return res.status(200).json({status: -1, message: "Account already exists"});
                        } else {
                            return res.status(200).json({status: -1, message: "Something went wrong"});
                        }
                    }
                    if(results.affectedRows == 1) {
                        return res.status(200).json({ status: 1, message: "Successfully created account" });
                    } else {
                        return res.status(200).json({ status: -1, message: "Account creation failed" });
                    }
                });
            });
        } catch(e) {
            return res.status(200).json({status: -1, message: "Cannot process your request"});
        }
    } else {
        return res.status(200).json({status: -1, message: "Invalid request"});
    }
});

module.exports = router

