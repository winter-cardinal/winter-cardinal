/*
 * Copyright (C) 2019 Toshiba Corporation
 * SPDX-License-Identifier: Apache-2.0
 */

(function () {
	"use strict";
	// Import
	var util = wcardinal.util.util;
	var Connectable = wcardinal.event.Connectable;

	var SScalar = wcardinal.controller.data.internal.SScalar;
	var SList = wcardinal.controller.data.SList;
	var SMovableList = wcardinal.controller.data.SMovableList;
	var SMap = wcardinal.controller.data.SMap;
	var SROQueue = wcardinal.controller.data.SROQueue;

	var MAX_PARTICLECONAINER_SYMBOLS = 50000;

	function isCircle(obj){
		if(obj.x == null) return false;
		if(obj.y == null) return false;
		if(obj.radius == null) return false;
		if(obj.color == null) return false;
		return true;
	}

	function isNewIndexRequired(args, connectable){
		var added = args[1];
		var removed = args[2];
		var updated = args[3];
		if(removed.length > 0) return true;
		if(added.length > 0) return true;
		if(connectable instanceof SMovableList){
			var newMoved = args[4];
			var oldMoved = args[5];
			if(newMoved.length > 0) return true;
			if(oldMoved.length > 0) return true;
		}
		return false;
	}


	function updateSymbols(stage, symbols, args, connectable, type){
		if(this instanceof SScalar){
			updateSymbol(symbols, connectable.toJson());
		}else if(connectable instanceof SList){
			var added = args[1];
			var removed = args[2];
			var updated = args[3];
			if(symbols.length == (added.length - removed.length)) return symbols;
			for(var i = removed.length - 1; i >= 0; --i){
				stage.removeChild(symbols[removed[i].index]);
				symbols.splice(removed[i].index, 1);
			}
			for(var i = 0, imax = added.length; i < imax; ++i){
				var symbol = addSymbol(newIndex.value, type);
				symbols.push(symbol);
				stage.addChild(symbol);
			}
			for(var i = 0, imax = updated.length; i < imax; ++i){
				var data = updated[i];
				updateSymbol(symbols[data.index], data.newValue);
			}
		}else if(connectable instanceof SMap){
			var added = args[1];
			var removed = args[2];
			var updated = args[3];
			util.each(removed, function(obj, key){
				if(added.hasOwnProperty(key)) return;
				if(updated.hasOwnProperty(key)) return;
				stage.removeChild(symbols[key]);
			})
			util.each(added, function(obj, key){
				if(symbols.hasOwnProperty(key)){
					updateSymbol(symbols[key], obj);
				}else{
					var symbol = addSymbol(obj, type);
					symbols[key] = symbol;
					stage.addChild(symbol);
				}
			})
			util.each(updated, function(obj, key){
				updateSymbol(symbols[key], obj.newValue);
			})
		}else if(connectable instanceof SROQueue){
		}
		return symbols;
	}


	function addSymbol(item, type){
		if(type == "circle") return initCircle(item);
		return null;
	}

	function createOriginCircleTexture(radius){
		var circle = new PIXI.Graphics();
		circle
		.beginFill(0xffffff)
		.drawCircle(0, 0, radius)
		.endFill();
		var texture = circle.generateCanvasTexture();
		return texture;
	};

	var originCircleTextures = {};

	var createCircleTexture = function(radius){
		if(originCircleTextures[radius] == null){
			originCircleTextures[radius] = createOriginCircleTexture(radius);
		}
		return originCircleTextures[radius];
	}

	var initCircle = function(item){
		var myCircle = new PIXI.Sprite();
		myCircle.position.x = item.x;
		myCircle.position.y = item.y;
		myCircle.texture = createCircleTexture(item.radius);
		myCircle.tint = item.color;
		createDragAndDropFor(myCircle);
		return myCircle;
	};

	var updateSymbol = function(symbol, item){
		symbol.position.set(item.x, item.y);
		symbol.tint = item.color;
	};


	var drag = false;
	var lastDragPosition = {x:0, y:0};
	var dragPosition = {x:0, y:0};
	function onMouseMove(e){
		if(drag){
			e.data.getLocalPosition(this.parent, dragPosition);
			var diffX = dragPosition.x - lastDragPosition.x;
			var diffY = dragPosition.y - lastDragPosition.y;
			this.position.x += diffX;
			this.position.y += diffY;
			e.data.getLocalPosition(this.parent, lastDragPosition);
		}
	}
	function createDragAndDropFor(target){
		target.interactive = true;
		target.buttonMode = true;
		target.on("mousedown", function(e){
			drag = true;
			e.data.getLocalPosition(this.parent, lastDragPosition);
			target.on("mousemove", onMouseMove)
		})
		target.on("mouseup", function(e){
			drag = false;
			target.off("mousemove");
		})
	}


	var bindAutowiredCircle = function(stage, autowired){
		var onValue = function(){};
		var circle;
		if(autowired.each){
			circle = [];
			autowired.each(function(obj, key){
				var newCircle = initCircle(obj);
				stage.addChild(newCircle);
				circle[key] = newCircle;
			});
			onValue = function(){
				circle = updateSymbols(stage, circle, arguments, this, "circle");
			}
		}else{
			circle = initCircle(autowired.toJson());
			stage.addChild(circle);
			onValue = function(){
				updateSymbol(circle, this.toJson());
			}
		}
		autowired.on("value", onValue);
		return circle;
	}


	var bindCircle = function(stage, object){
		var circle;
		if(isCircle(object)){
			circle = initCircle(object);
			stage.addChild(circle);
		}else{
			circle = [];
			util.each(object, function(obj, key){
				var newCircle = initCircle(obj);
				stage.addChild(newCircle);
				circle[key] = newCircle;
			})
		}
		return circle;
	}

	function initSymbols(stage, interactiveStage, data){
		if(data == null) return null;
		var symbols = {};
		util.each(data, function(obj, key){
			var target = obj.isInteractive ? interactiveStage : stage;
			var type = obj.type;
			var value = obj.value;
			var isConnectable = (value == null) ? false: (value instanceof Connectable);
			var symbol;
			switch(type){
				case "circle":
					if(isConnectable) symbol = bindAutowiredCircle(target, value);
					else symbol = bindCircle(target, value);
				break;
			}
			symbols[key] = symbol;
		})
		return symbols;
	}

	function _bindSymbols(symbol, data){
		util.each(data, function(obj, key){
			if(symbol[key] instanceof PIXI.Sprite){
				util.each(obj, function(primitiveVal, primitiveKey){
					var val = primitiveVal;
					Object.defineProperty(obj, primitiveKey, {
						get: function reactiveGetter(){
							return primitiveVal;
						},
						set: function reactiveSetter(newVal){
							primitiveVal = newVal;
							switch(primitiveKey){
								case "x":
								case "y":
									symbol[key].position[primitiveKey] = newVal;
									break;
								case "color":
									symbol[key].tint = newVal;
									symbol[key].cachedTint = newVal;
									break;
							}
						}
					})
				})
				var val = obj;
				Object.defineProperty(data, key, {
					get: function reactiveGetter(){
						return val;
					},
					set: function reactiveSetter(newVal){
						val = newVal;
						updateSymbol(symbol[key], newVal);
					}
				})
			}
			else _bindSymbols(symbol[key], obj.value);
		});
	}

	function bindSymbols(stage, interactiveStage, data){
		if(data == null) return null;
		var symbols = initSymbols(stage, interactiveStage, data);
		_bindSymbols(symbols, data);
		return symbols;
	}

	function setCircle(stage, target, value){
		if(target == null) return initSymbols(stage, {circle:value});
		else if(isCircle(value)){
			updateSymbol(target, value);
			return target;
		}else{
			var isEachable = false;
			util.each(value, function(obj, key){
				isEachable = true;
				if(target[key] == null){
					target[key] = initSymbols(stage, {circle:obj});
				}else{
					updateSymbol(target[key], obj);
				}
			})
			if(isEachable) return target;
			else return initSymbols(stage, {circle:value});
		}
	}

	var setQueue = function(args){
		var stage = args[0];
		var target = args[1];
		var value = args[2];
		util.each(value, function(obj, key){
			updateSymbol(target[key], obj);
		})
		/*
		switch(symbolType){
			case "circle":
				target = setCircle(stage, target, value);
				break;
			default:
				break;
		}
		*/
	}

	var performQueue = function(queue){
		switch(queue.type){
			case "set": setQueue(queue.args);
				break;
			default:
				break;
		}
	}

	var setStage = function(options){
		var canvasSize = options.pixi.canvasSize;
		var canvasId = options.pixi.canvasId;
		var stage = new PIXI.particles.ParticleContainer(MAX_PARTICLECONAINER_SYMBOLS, {position: true, tint: true});
		var renderer = PIXI.autoDetectRenderer(canvasSize.width, canvasSize.height,{
			backgroundColor: 0xffffff,
			view:document.getElementById(canvasId)
		});
		function renderPerFrame(){
			renderer.render(stage);
			window.requestAnimationFrame(renderPerFrame);
		}
		renderPerFrame();
		return stage;
	}

	var setRenderer = function(options){
		var canvasSize = options.pixi.canvasSize;
		var canvasId = options.pixi.canvasId;
		var renderer = PIXI.autoDetectRenderer(canvasSize.width, canvasSize.height,{
			backgroundColor: 0xffffff,
			view:document.getElementById(canvasId)
		});
		return renderer;
	}

	util.define("PixiConnector", [],{
		constructor: function(options){
			this.options = options;
			var renderer = this._renderer = setRenderer(options);
			var parentStage = new PIXI.Container();
			var stage = this._stage = new PIXI.particles.ParticleContainer(MAX_PARTICLECONAINER_SYMBOLS, {position: true, tint: true});
			parentStage.addChild(stage);
			var interactiveStage = this._interactiveStage = new PIXI.Container();
			parentStage.addChild(interactiveStage);
			this._symbols = {
				wcardinal: initSymbols(stage, interactiveStage, options.wcardinal),
				data: initSymbols(stage, interactiveStage, options.data),
				bind: bindSymbols(stage, interactiveStage, options.bind)
			};
			this._queue = [];

			function renderPerFrame(){
				renderer.render(parentStage);
				window.requestAnimationFrame(renderPerFrame);
			}
			renderPerFrame();
		},

		$nextTick: function(){
			var self = this;
			if(this._timer != null) return;
			this._timer = setTimeout(function(){
				while(self._queue.length > 0){
					var queue = self._queue.shift();
					performQueue(queue);
				}
				self._timer = null;
			}, 0);
		},
		set: function(chainedKey, value){
			var keys = chainedKey.split(".");
			if(keys[0] != "wcardinal" && keys[0] != "data") return this;
			var target = this._symbols;
			var options = this.options;
			options = options[keys[0]];
			target = target[keys.shift()];
			var isInteractive;
			for(var i = 0, imax = keys.length; i < imax; ++i){
				if(options.isInteractive != null) isInteractive = options.isInteractive;
				if(target[keys[i]] == null) break;
				target = target[keys[i]];
				options = options[keys[i]];
			}
			var stage = isInteractive ? this._interactiveStage : this._stage;
			var newQueue = {type:"set", args:[stage, target, value]};
			this._queue.push(newQueue);
			this.$nextTick();

			return this;
		}
	});
}());
