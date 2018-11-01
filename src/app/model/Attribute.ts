import {val} from "../helpers";
/**
 * User: phi
 * Date: 07.03.17
 * .  .___.
 * .  {o,o}
 * . /)___)
 * . --"-"--
 */
export class Attribute {
	id: number;
	name: string;
	description: string;
	interest: number;
	unit: string;

	/**
	 * Serialisierung.
	 * @param attributes_json
	 * @returns {Attribute[]}
	 */
	static fromJsons(attributes_json: any): Attribute[] {
		let attributes: Attribute[] = [];
		if(val(attributes_json)) {
			for(let attribute_json of attributes_json) {
				attributes.push(Attribute.fromJson(attribute_json));
			}
		}
		return attributes;
	}

	/**
	 * Serialisierung.
	 * @param attribute_json
	 * @returns {Attribute}
	 */
	static fromJson(attribute_json: any): Attribute {
		let attribute = Object.assign(new Attribute(), attribute_json);
		return attribute;
	}
}