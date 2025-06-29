import fs from "node:fs/promises";
import path from "node:path";
import readline from "node:readline/promises";
import { fileURLToPath } from "url";

(async () => {
	const dir = path.dirname(fileURLToPath(import.meta.url));
	const tocs = [];
	const bodies = [];
	for (const file of (await fs.readdir(dir)).sort()) {
		if (file.endsWith(".md")) {
			console.log(file);
			const fd = await fs.open(path.join(dir, file));
			try {
				const rl = readline.createInterface({
					input: await fd.createReadStream(),
					crlfDelay: Infinity
				});
				for await (const line of rl) {
					const trimmed = line.trim();
					if (trimmed.startsWith("##")) {
						const isSubSection = trimmed.startsWith("###");
						const label = line.substring(isSubSection ? 3 : 2).trim();
						const anchor = label.toLowerCase().replaceAll(/[^a-zA-Z0-9-\s]/g, "").replaceAll(/\s+/g, "-");
						tocs.push(`${isSubSection ? "\t" : ""}* [${label}](#${anchor})`);
					}
					bodies.push(line);
				}
				bodies.push("");
			} finally {
				await fd.close();
			}
		}
	}
	const content = [];
	content.push("# Cheatsheet\n");
	content.push("## Table of contents\n");
	for (const toc of tocs) {
		content.push(toc);
	}
	content.push("");
	for (const body of bodies) {
		content.push(body);
	}
	fs.writeFile(path.join(dir, "../all-in-one.md"), content.join("\n"), err => {
		if (err) {
			console.error(err);
		} else {
			// file written successfully
		}
	});
})();
