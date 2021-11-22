require('dotenv').config()
let router = require('express').Router();
const bodyParser = require('body-parser');
const roleAuth = require('../roleAuth');
const config = require('../config');
let connection = require('mysql2').createPool(config.database);

router.use(bodyParser.json());

router.post('/yolo', (req, res, next) => {
    res.status(200).json({bobo:req.body.yo});
})

router.get('/getUncompletedTrip', roleAuth.userAuth, (req, res, next) => {
    connection.getConnection((error, tempConnection) => {
        if(error) {
            console.log(error);
            res.statusCode = 200;
            return res.json({status: -1, message: "Something went wrong"});
        }
        let sqlQuery = `SELECT trip_id, destination, passenger_name, passenger_contact, booking_time FROM trips WHERE vehicle_id = ? AND completed = 0 ORDER BY booking_time DESC;`
        let fieldsArray = [req.user_id];
        tempConnection.query(sqlQuery, fieldsArray, async (error, results, fields) => {
            tempConnection.release();
            if(error) {
                console.log(error);
                return res.status(200).json({status: -1, message: "Something went wrong"});
            }
            if(results.length>0) {
                return res.status(200).json({status: 1, data: results, message: "Fetched uncompleted trips"});
            } else {
                return res.status(200).json({status: 0, message: "No uncompleted trips"});
            }
        });
    });
});

router.get('/getCompletedTrip', roleAuth.userAuth, (req, res, next) => {
    connection.getConnection((error, tempConnection) => {
        if(error) {
            console.log(error);
            res.statusCode = 200;
            return res.json({status: -1, message: "Something went wrong"});
        }
        let sqlQuery = `SELECT trip_id, destination, passenger_name, amount, booking_time FROM trips WHERE vehicle_id = ? AND completed = 1 ORDER BY booking_time DESC;`
        let fieldsArray = [req.user_id];
        tempConnection.query(sqlQuery, fieldsArray, async (error, results, fields) => {
            tempConnection.release();
            if(error) {
                console.log(error);
                return res.status(200).json({status: -1, message: "Something went wrong"});
            }
            if(results.length>0) {
                return res.status(200).json({status: 1, data: results, message: "Fetched uncompleted trips"});
            } else {
                return res.status(200).json({status: 0, message: "No uncompleted trips"});
            }
        });
    });
});

router.get('/getPayout', roleAuth.userAuth, (req, res, next) => {
    connection.getConnection((error, tempConnection) => {
        if(error) {
            console.log(error);
            res.statusCode = 200;
            return res.json({status: -1, message: "Something went wrong"});
        }
        let sqlQuery = `SELECT balance FROM users WHERE user_id = ?;`
        let fieldsArray = [req.user_id];
        tempConnection.query(sqlQuery, fieldsArray, async (error, results, fields) => {
            tempConnection.release();
            if(error) {
                console.log(error);
                return res.status(200).json({status: -1, message: "Something went wrong"});
            }
            if(results.length>0) {
                return res.status(200).json({status: 1, data: results[0], message: "Fetched uncompleted trips"});
            } else {
                return res.status(200).json({status: -1, message: "No data"});
            }
        });
    })
})

router.post('/completeTrip', roleAuth.userAuth, (req, res, next) => {
    const request = req.body;
    if(request.trip_id != null && request.otp != null) {
        connection.getConnection((error, tempConnection) => {
            if(error) {
                console.log(error);
                res.statusCode = 200;
                return res.json({status: -1, message: "Something went wrong"});
            }
            let sqlQuery = `SET @amt=-1;SELECT amount INTO @amt FROM trips WHERE trip_id = ? AND otp = ?;UPDATE trips SET completed = 1 WHERE trip_id = ? AND @amt != -1 AND completed = 0;UPDATE users SET balance = balance + @amt WHERE user_id = ? AND @amt != -1;SELECT @amt;`
            let fieldsArray = [request.trip_id, request.otp, request.trip_id, req.user_id];
            tempConnection.query(sqlQuery, fieldsArray, async (error, results, fields) => {
                tempConnection.release();
                if(error) {
                    console.log(error);
                    return res.status(200).json({status: -1, message: "Something went wrong"});
                }
                if(results[2].affectedRows == 1 && results[3].affectedRows == 1) {
                    return res.status(200).json({status: 1, message: "Your account has been credited with " + results[4][0]['@amt'] + " rupees"});
                } else {
                    return res.status(200).json({status: -1, message: "Please enter the correct details"});
                }
            });
        });
    } else {
        return res.status(200).json({status: -1, message: "Invalid request"});
    }
});

module.exports = router