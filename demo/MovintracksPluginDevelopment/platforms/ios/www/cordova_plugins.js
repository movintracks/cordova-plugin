cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "file": "plugins/org.apache.cordova.console/www/console-via-logger.js",
        "id": "org.apache.cordova.console.console",
        "clobbers": [
            "console"
        ]
    },
    {
        "file": "plugins/org.apache.cordova.console/www/logger.js",
        "id": "org.apache.cordova.console.logger",
        "clobbers": [
            "cordova.logger"
        ]
    },
    {
        "file": "plugins/com.movintracks.cordovamovintracks/movintracks.js",
        "id": "com.movintracks.cordovamovintracks.Movintracks",
        "clobbers": [
            "movintracks"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "org.apache.cordova.console": "0.2.13",
    "com.movintracks.cordovamovintracks": "1.0"
}
// BOTTOM OF METADATA
});