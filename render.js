var width = 900;
var height = 930;
var data = null;

var selected = null;

var nodes = null;
var lastStatus = [];
var positions = {};
var nodeMap = null;

var links = null;
var linkMap = null;
var subjectMap = null;
var simpliedLink = null;

var hotKey = null;
var orderedHotkey = null;
var words = ['i', 'me', 'my', 'myself', 'we', 'our', 'ours', 'ourselves', 'yo', 'your', 'yours', 'yourself', 'yourselves', 'he', 'him', 'his', 'himself', 'she', 'her', 'hers', 'herself', 'it', 'its', 'itself', 'they', 'them', 'their', 'theirs', 'themselves', 'what', 'which', 'who', 'whom', 'this', 'that', 'these', 'those', 'am', 'is', 'are', 'was', 'were', 'be', 'been', 'being', 'have', 'has', 'had', 'having', 'do', 'does', 'did', 'doing', 'a', 'an', 'the', 'and', 'but', 'if', 'or', 'because', 'as', 'until', 'while', 'of', 'at', 'by', 'for', 'with', 'about', 'against', 'between', 'into', 'through', 'during', 'before', 'after', 'above', 'below', 'to', 'from', 'up', 'down', 'in', 'out', 'on', 'off', 'over', 'under', 'again', 'further', 'then', 'once', 'here', 'there', 'when', 'where', 'why', 'how', 'all', 'any', 'both', 'each', 'few', 'more', 'most', 'other', 'some', 'such', 'no', 'nor', 'not', 'only', 'own', 'same', 'so', 'than', 'too', 'very', 's', 't', 'can', 'will', 'just', 'don', 'should', 'now', 'd', 'll', 'm', 'o', 're', 've', 'y', 'ain', 'aren', 'couldn', 'didn', 'doesn', 'hadn', 'hasn', 'haven', 'isn', 'ma', 'mightn', 'mustn', 'needn', 'shan', 'shouldn', 'wasn', 'weren', 'won', 'wouldn','','-','in','on','--','&','/','A','a','an','In','On','Of'];
var stopWords = null;

var svg = null;

var minMilliSecond;
var maxMilliSecond;

var center = { x: width / 2, y: height / 2 };


var week = {
	name : "week",
	start : (new Date("1998/11/9 00:00:00")).getTime(),
	end : (new Date("2002/6/16 00:00:00")).getTime(),
	step : 1000 * 60 * 60 * 24 * 7,
	min : 1,
	max : 175
};


var month = {
	name : "month",
	start : (new Date("1998/11/1 00:00:00")).getTime(),
	end : (new Date("2002/6/31 00:00:00")).getTime(),
	step : 1000 * 60 * 60 * 24 * 30,
	min : 1,
	max : 44
};

var year = {
	name : "year",
	start : (new Date("1998/1/1 00:00:00")).getTime(),
	end : (new Date("2002/12/31 00:00:00")).getTime(),
	step : 1000 * 60 * 60 * 24 * 365,
	min : 1,
	max : 5
};





d3.json("networks.json", function(error, result) {
	
	stopWords = new Set();
	for(let word of words){
		stopWords.add(word);
	}

	data = result;
	nodes = data.nodes;
	links = data.links;

	radialize(data.nodes.slice(12, 36), 300, 450);
	radialize(data.nodes.slice(0, 12), 120, 220);

	for(var index in nodes){
		lastStatus.push(nodes[index].visible);
	}

	console.log(links);

	nodes.forEach(function(d, i){
		positions[d.name] ={position : {x : d.x, y : d.y}};
	})

	minMilliSecond = d3.min(links, function(l) { return l.timestamp;} );
	maxMilliSecond = d3.max(links, function(l) { return l.timestamp;} );

	var min = (new Date("1998/11/9 00:00:00")).getTime();
	var max = (new Date("2002/12/31 23:59:59")).getTime();
	console.log(new Date(minMilliSecond));
	console.log(new Date(maxMilliSecond));
	console.log(new Date(min));
	console.log(new Date(max));
	console.log(minMilliSecond);
	console.log(maxMilliSecond);
	console.log(week.step);
	console.log(month.step);
	console.log(year.step);

	list = d3.select("#list");


	svg = d3.select("#graph")
				.append("svg")
				.attr("width", width)
				.attr("height", height);

	svg.append("g").attr("id", "links")
	svg.append("g").attr("id", "nodes")



	d3.select("#graph")
		.append("input")
		.attr("type", "range")
		.attr("id", "timeline");



	d3.select("#graph")
		.append("select")
		.attr("id", "selectSpan")
		.attr("onchange", "updateTimeLine(this.value)");

	d3.select("#selectSpan")
		.append("option")
		.attr("value", "week")
		.text("week");

	d3.select("#selectSpan")
		.append("option")
		.attr("value", "month")
		.text("month");

	d3.select("#selectSpan")
		.append("option")
		.attr("value", "year")
		.text("year");

	d3.select("#closeBtn")
		.on("click", function(){
			d3.select("#Subjects")
				.style("display", "none");
		});

	renderTimeLine(week);
	selected = week;
	updateData((week.min + week.max) / 2)
	//console.log(lastStatus);
	//console.log(links);
	//console.log(positions);
	
})



function renderTimeLine(interval){

	d3.select("#timeline")
		.attr("min", interval.min)
		.attr("max", interval.max)
		.attr("step", 1)
		.attr("defaultValue", (interval.min + interval.max) / 2)
		.attr("oninput","updateData(this.value)")
		.attr("style", "width : 900px");
	
}


function updateTimeLine(value){

	if(value == "week"){
		renderTimeLine(week);
		selected = week;
	} else if(value == "month"){
		renderTimeLine(month);
		selected = month;
	} else{
		renderTimeLine(year);
		selected = year;
	}

}


function updateData(value){

	var start = 0, end = 0;
	if(selected.name == "week"){
		//console.log("week");
		start = week.start + (week.step * (value - 1));
		end = start + week.step;
	} else if(selected.name == "month"){
		//console.log("month");
		start = month.start + (month.step * (value - 1));
		end = start + month.step;
	} else{
		//console.log("year");
		start = year.start + (year.step * (value - 1));
		end = start + year.step;
	}
	//console.log(value + "...." + start + "......" + end);
	
	var startDate = new Date(start);
	var endDate = new Date(end);
	
	d3.select("#time")
		.select("text")
		.text(function(d) {
			return  startDate.getDate() + "/" + startDate.getMonth() + " " + startDate.getFullYear() + "  —————  " + endDate.getDate() + "/" + endDate.getMonth() + " " + endDate.getFullYear();
		});


	//console.log((new Date(start)) + "...." + (new Date(end)));
	updateLinks(start, end);
	updateNodes(start, end);
	//console.log(links.length);
	//console.log(nodes);
	renderLinks(simpliedLink);

}



function updateNodes(start, end){

	appearedNodes = new Set();
	for(let link of links){
		//console.log(links[index]);
		appearedNodes.add(data.nodes[link.target]);
		appearedNodes.add(data.nodes[link.source]);
	}

	//console.log(appearedNodes);

	for(let node of nodes){
		if(!appearedNodes.has(node))
			node.visible = false;
		else
			node.visible = true;
	}

}



function updateLinks(start, end){

	linkTemp = data.links;
	links = [];
	//console.log(linkTemp);
	linkTemp.forEach(function(link) {
		//console.log(start + "  " + link.timestamp + "   " + end);
		if(link.timestamp >= start && link.timestamp <= end){
			links.push(link);
		}
	});

	linkMap = new Map();
	subjectMap = new Map();
	nodeMap = new Map();
	hotKey = new Map();
	for(let link of links){
		var key = null;
		var source = parseInt(link.source);
		var target = parseInt(link.target);
		if(source > target)
			key = source + "." + target;
		else
			key = target + "." + source;
		if(linkMap.get(key) == undefined)
			linkMap.set(key, 0);
		linkMap.set(key, linkMap.get(key) + 1);

		if(subjectMap[key] == undefined)
			subjectMap[key] = new Array();
		var date = new Date(link.timestamp);
		subjectMap[key].push(date.getDate() + "/" + date.getMonth() + " " + date.getFullYear() + " " +　date.getHours() + ":" + date.getMinutes() + "　　" + link.subject);


		if(nodeMap.get(link.source) == undefined)
			nodeMap.set(link.source, 0);
		if(nodeMap.get(link.target) == undefined)
			nodeMap.set(link.target, 0);
		nodeMap.set(link.source, nodeMap.get(link.source) + 1);
		nodeMap.set(link.target, nodeMap.get(link.target) + 1);


		var temp = link.subject.split(":");
		var keys = temp[temp.length - 1].split(" ");
		for(let key of keys){
			key = key.trim();
			if(!stopWords.has(key)){
				if(hotKey.get(key) == undefined)
					hotKey.set(key, 0);
				hotKey.set(key, hotKey.get(key) + 1);
			}
		}

	}

	//console.log(subjectMap);

	var tuples = [];

	for (let key of hotKey.keys()) tuples.push([key, hotKey.get(key)]);

	tuples.sort(function(a, b) {
		var value1 = a[1];
		var value2 = b[1];

		return value1 > value2 ? -1 : (value1 < value2? 1 : 0);
	});

	orderedHotkey = [];

	for (var i = 0; i < d3.min([tuples.length, 50]); i++) {
		var key = tuples[i][0];
		var value = tuples[i][1];
		orderedHotkey.push([key, value]);
	}

	//console.log(orderedHotkey);

	simpliedLink = [];
	for (var [key, value] of linkMap.entries()) {
		var tempLink = new Object();
		tempLink = {
			source : key.split(".")[0],
			target : key.split(".")[1],
			weight : value
		};
		simpliedLink.push(tempLink);
	}

	console.log(nodeMap);
	console.log(linkMap);
	console.log(simpliedLink);

}




function renderLinks(updatedData){

	var weights = Array.from(linkMap.values());

	var minLink = d3.min(weights);
	var maxLink = d3.max(weights);
	//console.log(maxLink);

	var linear = d3.scale.linear()
					.domain([minLink, maxLink])
					.range([1, 6]);

	var update = svg.select("#links")
					.selectAll(".link")
					.data(updatedData);

	var enter = update.enter();
	var exit = update.exit();


	enter
		.append("line")
		.attr("class", "link")
		.attr("stroke","black")
		.attr("stroke-opacity","0.6")
		.attr("x1", function(d) { return data.nodes[d.source].x;})
		.attr("y1", function(d) { return data.nodes[d.source].y;})
		.attr("x2", function(d) { return data.nodes[d.target].x;})
		.attr("y2", function(d) { return data.nodes[d.target].y;})
		.attr("stroke-linecap", "round")
		.attr("stroke-width", function(d) { return linear(d.weight); })
		.on("mouseover", function(d){
			this.setAttribute("stroke-width", linear(d.weight) + 2);
		})
		.on("mouseleave", function(d){
			this.setAttribute("stroke-width", linear(d.weight));
		})
		.on("click", function(d) {
			showSubjectList(d);
			//console.log(d.weight);
		});


	update
		.attr("x1", function(d) { return data.nodes[d.source].x;})
		.attr("y1", function(d) { return data.nodes[d.source].y;})
		.attr("x2", function(d) { return data.nodes[d.target].x;})
		.attr("y2", function(d) { return data.nodes[d.target].y;})
		.attr("stroke-width", function(d) { return linear(d.weight); })
		.on("mouseover", function(d){
			this.setAttribute("stroke-width", linear(d.weight) + 2);
		})
		.on("mouseleave", function(d){
			this.setAttribute("stroke-width", linear(d.weight));
		})
		.on("click", function(d) {
			showSubjectList(d);
			//console.log(d.weight);
		}); 
	
	exit.remove();

	renderNodes(nodes);


}

function renderNodes(updatedData){

	var weights = Array.from(nodeMap.values());
	var minNode = d3.min(weights);
	var maxNode = d3.max(weights);
	var linear = d3.scale.linear()
					.domain([minNode, maxNode])
					.range([10, 30]);
	//console.log(nodeMap.get(5));

	var nodeUpdate = svg.select("#nodes")
						.selectAll(".node")
						.data(updatedData);

	var nodeEnter = nodeUpdate.enter();


	nodeEnter
		.append("circle")
		.attr("class", "node")
		.attr("fixed", "true")
		.transition()
		.delay(200)
		.duration(500)
		.ease("linear")
		.attr("r", function(d, i){
			if(nodeMap.get(i) != undefined)
				return linear(nodeMap.get(i));
			else
				return 10;
		})
		.attr("fill-opacity", 1)
		.attr("cx",function(d){ return d.x; })
		.attr("cy",function(d){ return d.y; })
		.style("fill", function(d, i) {
			if(lastStatus[i] == false && d.visible == true){
				d.color = "orange";
				return "#FF9224";
			}
			if(lastStatus[i] == true && d.visible == true){
				d.color = "green";
				return "#93FF93";
			}
			else{
				d.color = "grey";
				return "#BEBEBE";
			}
			//if(lastStatus[i] == true && d.visible == false)


		});
		//.attr("onmousemove", function(d) { console.log(d.x + "...." + d.y);})


	nodeUpdate
		.on("mouseenter", function(d, i){
			hightlight(d.name,d.color);
		})
		.on("mouseleave", function(d, i){
			unHightLight();
		})
		.transition()
		.delay(200)
		.duration(500)
		.ease("linear")
		.attr("r", function(d, i){
			if(nodeMap.get(i) != undefined)
				return linear(nodeMap.get(i));
			else
				return 10;
		})
		.attr("cx",function(d){ return d.x; })
		.attr("cy",function(d){ return d.y; })
		.attr("opacity", "1")
		.style("fill", function(d, i) {
			if(lastStatus[i] == false && d.visible == true){
				d.color = "orange";
				return "#FF9224";
			}
			if(lastStatus[i] == true && d.visible == true){
				d.color = "green";
				return "#93FF93";
			}
			else{
				d.color = "grey";
				return "#BEBEBE";
			}
			//if(lastStatus[i] == true && d.visible == false)
		});


	nodeEnter
		.append("text")
		.attr("class","nodetext")
		.attr("x",function(d){ return d.x; })
		.attr("y",function(d, i){ 
			if(nodeMap.get(i) != undefined)
				return (d.y - linear(nodeMap.get(i)) - 2); 
			else
				return (d.y - 7); 
		})
		.attr("id",function(d) { return d.name; })
		.style("text-anchor", "middle")
		.text(function(d, i) { return d.name; })
		.on("mouseenter", function(d, i){
			hightlight(d.name,d.color);
		})
		.on("mouseleave", function(d, i){
			unHightLight();
		});


	d3.selectAll(".nodetext")
	  .text(function(d) {
		    if(d.visible == false) 
				return ""; 
			else
				return d.name;
		})
	  .attr("y",function(d, i){ 
			if(nodeMap.get(i) != undefined)
				return (d.y - linear(nodeMap.get(i)) - 2); 
			else
				return (d.y - 7); 
	  });

	renderNodeList(updatedData, lastStatus);
	renderHotkeys(orderedHotkey);

	for(var index in updatedData){
		lastStatus[index] = updatedData[index].visible;
	}
	//console.log(lastStatus);

}


function renderNodeList(updatedData, lastStatus){

	var orangeNode = [];
	var greyNode = [];
	var greenNode = [];

	for(let node of nodes){
		if(node.color == "grey")
			greyNode.push(node);
		else if(node.color == "orange")
			orangeNode.push(node);
		else if(node.color == "green")
			greenNode.push(node);
	}

	//console.log(lastStatus);
	var orange = d3
                .select(".orange")
				.selectAll("li")
                .data(orangeNode);

	orange
		.enter()
		.append("li")
		.attr('class','orange')
		.attr('class','list-group-item')
		.on("mouseenter", function(d, i){
			hightlight(d.name, "orange");
		})
		.on("mouseleave", function(d, i){
			unHightLight();
		})
		.transition()
		.duration(1000)
		.ease("linear")
		.text(function(d, i) { return d.name; });
		
	orange.text(function(d, i) { return d.name; });


	orange.exit().remove();

	var grey = d3
                .select(".grey")
				.selectAll("li")
                .data(greyNode);

	grey
		.enter()
		.append("li")
		.attr('class','grey')
		.attr('class','list-group-item')
		.on("mouseenter", function(d, i){
			hightlight(d.name,"grey");
		})
		.on("mouseleave", function(d, i){
			unHightLight();
		})
		.transition()
		.duration(1000)
		.ease("linear")
		.text(function(d, i) { return d.name; });
		
	grey.text(function(d, i) { return d.name; });


	grey.exit().remove();


	var green = d3
                .select(".green")
				.selectAll("li")
                .data(greenNode);

	//console.log(green);
	green
		.enter()
		.append("li")
		.attr('class','green')
		.attr('class','list-group-item')
		.on("mouseenter", function(d, i){
			hightlight(d.name,"green");
		})
		.on("mouseleave", function(d, i){
			unHightLight();
		})
		.transition()
		.duration(1000)
		.ease("linear")
		.text(function(d, i) { return d.name; });
		
	green.text(function(d, i) { return d.name; });


	green.exit().remove();

	//console.log(d3.selectAll("li"));

}



function renderHotkeys(updatedData){
	//console.log(updatedData);
	var hotkey = d3
                .select(".hotkey")
				.selectAll("li")
                .data(updatedData);

	hotkey
		.enter()
		.append("li")
		.attr('class','list-group-item')
		.text(function(d) { return d[0]})
		.append("span")
		.attr('class','badge')
		.text(function(d) { return d[1]})
		.transition()
		.delay(500)
		.duration(1000)
		.ease("linear");
	
	hotkey
		.attr('class','list-group-item')
		.text(function(d) { return d[0]})
		.append("span")
		.attr('class','badge')
		.text(function(d) { return d[1]})
		.transition()
		.delay(500)
		.duration(1000)
		.ease("linear");


	hotkey.exit().remove();
}



function showSubjectList(link){
	
	var key = null;
	var source = parseInt(link.source);
	var target = parseInt(link.target);
	if(source > target)
		key = source + "." + target;
	else
		key = target + "." + source;
	//console.log(subjectMap);
	//console.log(key);
	//console.log(link.source);
	//console.log(link.target);
	var subjects = subjectMap[key];
	var str = "";
	for(let subject of subjects){
		str = str + subject + "\n";
	}
	
	d3.select("#subjectList")
		.text(str);
		
	d3.select("#Subjects")
		.style("display", "block" );
		
}




function hightlight(name,color){
	d3.selectAll("circle")
		.style('stroke', function(d) {
			return (name === d.name? "grey" : undefined);
		})
		.style("fill", function(d) {
			if(name === d.name){
				if(d.color == "orange")
					return "#D2691E";
				else if(d.color == "grey")
					return "#7B7B7B";
				else if(d.color == "green")
					return "#00DB00";
			}
			if(d.color == "orange")
				return "#FF9224";
			if(d.color == "grey")
				return "#BEBEBE";
			if(d.color == "green")
				return "#93FF93";
		})
		.style('stroke-width',2);

	d3.selectAll("li")
		.style('background-color', function(d) {
			if(name === d.name){
				if(d.color == "orange")
					return "#D2691E";
				else if(d.color == "grey")
					return "#7B7B7B";
				else if(d.color == "green")
					return "#00DB00";
			}
			if(d.color == "orange")
				return "#FF9224";
			if(d.color == "grey")
				return "#BEBEBE";
			if(d.color == "green")
				return "#93FF93";
		})
		.style('color', function(d) {
			return (name === d.name? "white" : undefined);
		});
}


function unHightLight(){
	d3.selectAll("circle")
		.style('stroke', undefined)
		.style("fill", function(d) {
			if(d.color == "orange")
				return "#FF9224";
			if(d.color == "grey")
				return "#BEBEBE";
			if(d.color == "green")
				return "#93FF93";
		});

	d3.selectAll("li")
		.style('background-color',  undefined)
		.style('color', undefined);

}



function radialize(nodes, inner, outter){

	//console.log(nodes.length);
	var increment = 360 / 24;
	var radius, angle = 0;
	nodes.forEach(function(d){
		angle += increment;
		radius = inner + (outter - inner) * Math.random();
		coordinates = radialLocation(center, angle, radius);
		//console.log(coordinates);
		d.x = coordinates.x;
		d.y = coordinates.y;
		d.visible = false;
		d.color = undefined;
		d.weight = 0;
	});

}

function radialLocation(center, angle, radius) {

	var x, y;
	x = center.x + radius * Math.cos(angle);
	y = center.y + radius * Math.sin(angle);
	//console.log(x + "," + y);
	return {
	  "x": x,
	  "y": y
	};

};
