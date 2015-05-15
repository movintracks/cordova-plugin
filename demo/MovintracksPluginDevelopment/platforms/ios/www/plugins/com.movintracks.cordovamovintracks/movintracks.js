cordova.define("com.movintracks.cordovamovintracks.Movintracks", function(require, exports, module) { cordova.define("cordova/plugin/movintracks", function(require, exports, module) {
	movintracks = {};

	/**
	 * Create a Movintracks instance with basic configuration from movintracks.json
	 */
	movintracks.init = function(successCallback, failureCallback) {
		cordova.exec(successCallback, failureCallback, "MovintracksPlugin", "initMovintracks", []);
	};

	/**
	 * Force download data of campaigns
	 */
	movintracks.downloadData = function(successCallback, failureCallback) {
		cordova.exec(successCallback, failureCallback, "MovintracksPlugin", "downloadData", []);
	};

	/**
	 * Get number of beacons availables
	 */
	movintracks.getBeaconsAvailable = function(successCallback, failureCallback) {
		cordova.exec(successCallback, failureCallback, "MovintracksPlugin", "getBeaconsAvailable", []);
	};

	/**
	 * Configure custom callback
	 */
	movintracks.customCallBackAction = function(nameOfCustomCallback, successCallback, failureCallback) {
		cordova.exec(successCallback, failureCallback, "MovintracksPlugin", "customCallBackAction", [nameOfCustomCallback]);
	};
	module.exports = movintracks;
});

});
