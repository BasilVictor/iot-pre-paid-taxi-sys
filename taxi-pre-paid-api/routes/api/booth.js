require('dotenv').config()
let router = require('express').Router();
const bodyParser = require('body-parser');
const roleAuth = require('../roleAuth');
const config = require('../config');
let connection = require('mysql2').createPool(config.database);
const fast2sms = require('fast-two-sms');

router.use(bodyParser.json());

router.post('/addToQueue', roleAuth.boothAuth, (req, res, next) => {
    const request = req.body;
    if(request.uid != null) {
        connection.getConnection((error, tempConnection) => {
            if(error) {
                console.log(error);
                res.statusCode = 200;
                return res.json({status: -1, message: "Something went wrong"});
            }
            let sqlQuery = `SET @vid=-1, @queue_vid=-1;SELECT user_id INTO @vid FROM users WHERE uid = ?;SELECT vehicle_id INTO @queue_vid FROM queue WHERE vehicle_id = @vid; INSERT INTO queue (booth_id, vehicle_id, entry_time) SELECT ?,@vid,UNIX_TIMESTAMP()*1000 WHERE @vid != -1 AND @queue_vid = -1;SELECT @queue_vid;`
            let fieldsArray = [request.uid, req.user_id];
            tempConnection.query(sqlQuery, fieldsArray, async (error, results, fields) => {
                tempConnection.release();
                if(error) {
                    console.log(error);
                    return res.status(200).json({status: -1, message: "Something went wrong"});
                }
                if(results[3].insertId > 0) {
                    return res.status(200).json({status: 1, message: "Taxi added to queue"});
                } else if (results[4][0]['@queue_vid'] != -1) {
                    return res.status(200).json({status: -1, message: "Taxi already in queue"});
                } else {
                    return res.status(200).json({status: -1, message: "Taxi does not exist in system"});
                }
            });
        });
    } else {
        return res.status(200).json({status: -1, message: "Invalid request"});
    }
});

router.post('/bookTaxi', roleAuth.boothAuth, (req, res, next) => {
    const request = req.body;
    if(request.passenger_name != null && request.destination != null && request.passenger_phone != null && request.distance != null) {
        connection.getConnection((error, tempConnection) => {
            if(error) {
                console.log(error);
                res.statusCode = 200;
                return res.json({status: -1, message: "Something went wrong"});
            }
            let otp = getOtp();
            let sqlQuery = `SET @vid=-1,@phone=-1;SELECT vehicle_id INTO @vid FROM queue WHERE booth_id = ? ORDER BY entry_time DESC LIMIT 1; SELECT mobile INTO @phone FROM users WHERE user_id = @vid AND @vid != -1; DELETE FROM queue WHERE vehicle_id = @vid AND @vid != -1; INSERT INTO trips (source_booth_id, destination, vehicle_id, passenger_name, passenger_contact, otp, amount, booking_time) SELECT ?,?,@vid,?,?,?,?,UNIX_TIMESTAMP()*1000 WHERE @vid != -1;SELECT @vid, @phone;`
            let fieldsArray = [req.user_id, req.user_id, request.destination, request.passenger_name, request.passenger_phone, otp, getCost(request.distance)];
            tempConnection.query(sqlQuery, fieldsArray, async (error, results, fields) => {
                tempConnection.release();
                if(error) {
                    console.log(error);
                    return res.status(200).json({status: -1, message: "Something went wrong"});
                }
                if(results[5][0]['@vid'] != -1) {
                    var options = {
                        authorization : process.env.SMS_KEY,
                        message : 'Your pre-paid taxi has been booked.\nTaxi Number: ' + results[5][0]['@vid'] + '\nDriver Contact: ' + results[5][0]['@phone'] + '\nOTP of trip: ' + otp + '\nOnly share the OTP AFTER you have completed your journey.',
                        numbers : [request.passenger_phone]
                    };
                    fast2sms.sendMessage(options);
                    return res.status(200).json({status: 1, data : { vehicle_id: results[5][0]['@vid'] }, message: "Taxi booked successfully"});
                } else {
                    return res.status(200).json({status: -1, message: "No taxi in queue, please wait"});
                }
            });
        });
    } else {
        return res.status(200).json({status: -1, message: "Invalid request"});
    }
});

router.get('/getCost/:distance', roleAuth.boothAuth, (req, res, next) => {
    const request = req.params;
    if(request.distance != null) {
        cost = getCost(request.distance);
        return res.status(200).json({status: 1, data: { cost: cost }, message: ""});
    } else {
        return res.status(200).json({status: -1, message: "Invalid request"});
    }
});

// TODO: Clear queue from booth end

function getOtp() {
    return Math.floor(Math.random() * (999999 - 100000 + 1) + 100000);
}

function getCost(distance) {
    let km = Math.floor(distance/1000)
    var cost = 0
    if(km < 2) {
        cost = 60
    } else if(km < 4) {
        cost = 110
    } else if(km < 6) {
        cost = 170
    } else if(km < 8) {
        cost = 230
    } else if(km < 10) {
        cost = 250
    } else if(km < 12) {
        cost = 300
    } else if(km < 14) {
        cost = 350
    } else if(km  < 16) {
        cost = 400
    } else if(km > 16) {
        cost = 480
    }
    return cost;
}

module.exports = router