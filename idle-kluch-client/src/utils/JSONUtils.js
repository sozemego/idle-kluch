/**
 * Attempts to parse a string into an object.
 * If it cannot, returns an empty object.
 * @param body
 * @returns {{}}
 */
export const parseJSON = (body) => {
	if (typeof body !== 'string') return {};//consider throwing error here
	try {
		return JSON.parse(body);
	} catch (error) {
		return {};
	}
};