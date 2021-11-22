const router = require('express').Router();

router.use("/auth", require("./auth"));
router.use("/booth", require("./booth"));
router.use("/driver", require("./driver"));

module.exports = router;