import resolve from 'rollup-plugin-node-resolve';
import commonjs from 'rollup-plugin-commonjs';
import { version } from './package.json';
import { terser } from "rollup-plugin-terser";
import path from 'path';
import del from 'rollup-plugin-delete';
import copy from 'rollup-plugin-copy';
import sourcemaps from 'rollup-plugin-sourcemaps';
import fs from 'fs';

// In/out directories
const SOURCE_DIR = 'src/main/javascript/';
const OUTPUT_FILE = 'dist/wcardinal';
const META_INF_DIR = 'src/main/resources/META-INF/resources/webjars/wcardinal/';
const META_INF_OUTPUT = `${META_INF_DIR}${version}/`;

// License header
const LICENSE_HEADER_LINES = fs.readFileSync( './LICENSE_HEADER', 'UTF-8' ).split( '\n' );
const LICENSE_HEADER_LINES_LENGTH = LICENSE_HEADER_LINES.length;
if( 0 < LICENSE_HEADER_LINES_LENGTH && LICENSE_HEADER_LINES[ LICENSE_HEADER_LINES_LENGTH - 1 ].trim().length <= 0 ) {
	LICENSE_HEADER_LINES.splice( LICENSE_HEADER_LINES_LENGTH - 1, 1 );
}
const LICENSE_HEADER = LICENSE_HEADER_LINES.join( '\n ' );

// Banner
const BANNER =
`/*
 Winter Cardinal v${version}
 ${LICENSE_HEADER}

 Promise Polyfill
 Copyright (C) 2014 Taylor Hakes and Forbes Lindesay.
*/`

// Terser options
const TERSER_OPTIONS = {
	compress: {
		passes: 3
	},
	output: {
		preamble: BANNER
	},
	mangle: {
		properties: {
			regex: /(^_[\$\w]+[A-Za-z]$|^[A-Za-z]\w+_$)/,
			reserved: ['_immediateFn', '_element', '_$element', '_settings']
		}
	}
};

// Rollup settings
export default ( !process.env.ROLLUP_WATCH ?
	[{
		input: SOURCE_DIR + 'wcardinal.browser.js',
		output: [{
			name: 'wcardinal',
			file: OUTPUT_FILE + '.js',
			format: 'iife',
			banner: BANNER
		}],
		plugins: [
			del({ targets: [
				'dist/*.js',
				'dist/*.js.map',
				META_INF_DIR
			]}),
			resolve(),
			commonjs(),
		]
	},{
		input: SOURCE_DIR + 'wcardinal.js',
		output: [{
			file: OUTPUT_FILE + '.cjs.js',
			format: 'cjs',
			banner: BANNER
		}, {
			file: OUTPUT_FILE + '.esm.js',
			format: 'es',
			banner: BANNER
		}],
		plugins: [
			resolve(),
			commonjs()
		]
	},{
		input: SOURCE_DIR + 'wcardinal.browser.js',
		output: [{
			name: 'wcardinal',
			file: OUTPUT_FILE + '.min.js',
			format: 'iife',
			sourcemap: true,
			sourcemapPathTransform: ( relativePath ) => {
				return path.relative( "../src/main/typescript/", relativePath )
			},
		}],
		plugins: [
			sourcemaps(),
			resolve(),
			commonjs(),
			terser( TERSER_OPTIONS )
		]
	},{
		input: SOURCE_DIR + 'wcardinal.browser.worker.js',
		output: [{
			name: 'wcardinal',
			file: OUTPUT_FILE + '.worker.js',
			format: 'iife',
			banner: BANNER
		}],
		plugins: [
			resolve(),
			commonjs()
		]
	},{
		input: SOURCE_DIR + 'wcardinal.worker.js',
		output: [{
			file: OUTPUT_FILE + '.worker.cjs.js',
			format: 'cjs',
			banner: BANNER
		}, {
			file: OUTPUT_FILE + '.worker.esm.js',
			format: 'es',
			banner: BANNER
		}],
		plugins: [
			resolve(),
			commonjs()
		]
	},{
		input: SOURCE_DIR + 'wcardinal.browser.worker.js',
		output: [{
			name: 'wcardinal',
			file: OUTPUT_FILE + '.worker.min.js',
			format: 'iife',
			sourcemap: true,
			sourcemapPathTransform: ( relativePath ) => {
				return path.relative( "../src/main/typescript/", relativePath )
			}
		}],
		plugins: [
			sourcemaps(),
			resolve(),
			commonjs(),
			terser( TERSER_OPTIONS )
		]
	},{
		input: SOURCE_DIR + 'wcardinal.in-worker.js',
		output: [{
			file: OUTPUT_FILE + '.in-worker.js',
			format: 'iife',
			banner: BANNER
		}],
		plugins: [
			resolve(),
			commonjs()
		]
	},{
		input: SOURCE_DIR + 'wcardinal.in-worker.js',
		output: [{
			file: OUTPUT_FILE + '.in-worker.cjs.js',
			format: 'cjs',
			banner: BANNER
		}, {
			file: OUTPUT_FILE + '.in-worker.esm.js',
			format: 'es',
			banner: BANNER
		}],
		plugins: [
			resolve(),
			commonjs()
		]
	},{
		input: SOURCE_DIR + 'wcardinal.in-worker.js',
		output: [{
			file: OUTPUT_FILE + '.in-worker.min.js',
			format: 'iife',
			sourcemap: true,
			sourcemapPathTransform: ( relativePath ) => {
				return path.relative( "../src/main/typescript/", relativePath )
			}
		}],
		plugins: [
			sourcemaps(),
			resolve(),
			commonjs(),
			terser( TERSER_OPTIONS ),
			copy({
				targets: [
					{ src: 'dist/wcardinal.in-worker.js', dest: META_INF_OUTPUT },
					{ src: 'dist/wcardinal.in-worker.min.js', dest: META_INF_OUTPUT },
					{ src: 'dist/wcardinal.in-worker.min.js.map', dest: META_INF_OUTPUT },
					{ src: 'dist/wcardinal.js', dest: META_INF_OUTPUT },
					{ src: 'dist/wcardinal.min.js', dest: META_INF_OUTPUT },
					{ src: 'dist/wcardinal.min.js.map', dest: META_INF_OUTPUT },
					{ src: 'dist/wcardinal.worker.js', dest: META_INF_OUTPUT },
					{ src: 'dist/wcardinal.worker.min.js', dest: META_INF_OUTPUT },
					{ src: 'dist/wcardinal.worker.min.js.map', dest: META_INF_OUTPUT }
				],
				hook: 'writeBundle'
			})
		]
	}] :
	[{
		input: SOURCE_DIR + 'wcardinal.browser.js',
		output: [{
			name: 'wcardinal',
			file: OUTPUT_FILE + '.js',
			format: 'iife',
			banner: BANNER
		}],
		plugins: [
			resolve(),
			commonjs(),
			copy({
				targets: [
					{ src: 'dist/wcardinal.js', dest: META_INF_OUTPUT },
					{ src: 'dist/wcardinal.js', dest: META_INF_OUTPUT, rename: 'wcardinal.min.js' }
				],
				hook: 'writeBundle'
			})
		]
	}]
);
