
var expect = require("chai").expect;
var Sybase = require("../src/SybaseDB.js");
var P = require("bluebird");

//Configure To Connect To Your database here:
var host = '10.0.0.141',
	port = 5000,
	user = 'sa',
	pw = '',
	db = "exchange_wl1";

describe("Node Sybase Bridge", function() {
	
	var subject;
	var connectError;
	
	before(function(done){
		subject = new Sybase(host, port, db, user, pw, true);	
		subject.connect(function(err) {
			connectError = err;
			done();
		});
	});

	after(function(done) {
		subject.disconnect();
		done();
	});

	it("Connect", function(done) {
		expect(connectError).to.equal(null);
		expect(subject.isConnected()).to.equal(true);
		done();
	});

	it("Simple Single Array Result", function(done) {
		
		if (!subject.isConnected()) {
			expect(connectError).to.equal(null);
			done();
			return;	
		}

		subject.query("select top 1 * from accounts", function(err, data) {

			expect(err).to.equal(null);

			expect(data).to.be.a('array');
			expect(data.length).to.equal(1);			
			done();
		});

	});

	it('Should work with updates', function(done) {
		
		if (!subject.isConnected()) {
			expect(connectError).to.equal(null);
			done();
			return;	
		}

		var pquery = P.promisify(subject.query, {context: subject});
		pquery("update accounts set email = 'newemail@gmail.com' where name = 'testuser17'").then(function(results){
			
			console.log('updates returned: ' + JSON.stringify(results));
			console.dir(results);

			done();
		}).catch((err)=>{
			done(err);
		});		
	});

	it('Should work with inserts', function(done) {
		
		if (!subject.isConnected()) {
			expect(connectError).to.equal(null);
			done();
			return;	
		}

		var pquery = P.promisify(subject.query, {context: subject});
		pquery("select top 2 * from app_log\ninsert into app_log (target, date, lvel, message) values ('testing', getdate(), 'ERROR', 'msg')\ninsert into app_log (target, date, lvel, message) values ('testing2', getdate(), 'ERROR', 'msg')\nselect top 2 * from app_log").then(function(results){
			
			console.log('inserts returned: ' + JSON.stringify(results));
			console.dir(results);

			done();
		}).catch((err)=>{
			done(err);
		});		
	});

	it('Should work with stored procedres', function(done) {
		
		if (!subject.isConnected()) {
			expect(connectError).to.equal(null);
			done();
			return;	
		}

		var pquery = P.promisify(subject.query, {context: subject});
		pquery("exec sp_test").then(function(results){
			
			console.log('inserts returned: ' + JSON.stringify(results));
			console.dir(results);

			done();
		}).catch((err)=>{
			done(err);
		});		
	});
	
	it("Multiple async Calls (batch)", function(done) {
		
		if (!subject.isConnected()) {
			expect(connectError).to.equal(null);
			done();
			return;	
		}

		var pquery = P.promisify(subject.query, {context: subject});

		var pArray = [];

		for (var i=0; i<5; i++)
		{
			pArray.push(pquery("select top 1 * from accounts"));
		}

		P.all(pArray).then(function(results) {
			
			console.log(JSON.stringify(results));

			results.forEach(function(data) {
				expect(data).to.be.a('array');
				expect(data.length).to.equal(1);				
			});
			done();

		}).catch(function(err) {
			done(err);
		});
	});

	it("Batch with one error", function(done) {
		
		if (!subject.isConnected()) {
			expect(connectError).to.equal(null);
			done();
			return;	
		}

		var pquery = P.promisify(subject.query, {context: subject});

		var pArray = [];

		var badEgg = pquery("select * from tableThatDoesntExist");
		for (var i=0; i<5; i++)
		{
			pArray.push(pquery("select top 1 * from accounts"));
		}
		
		P.all(pArray).then(function(results) {
			
			console.log(JSON.stringify(results));

			results.forEach(function(data) {
				expect(data).to.be.a('array');
				expect(data.length).to.equal(1);				
			});

		}).catch(function(err) {
			done(err);
		});

		badEgg.then(function(results) {
			done(new Error("Expected an error from this call."));
		}).catch(function(err) {
			//console.log("error:" + err.message);
			expect(err.message).to.contain("tableThatDoesntExist");
			done();
		});
	});

});