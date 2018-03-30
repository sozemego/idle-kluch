import networkService from '../api/NetworkService';

const basePath = '/user';
const registerPath = `${basePath}/register`;
const isAvailablePath = `${basePath}/single/available`;
const loginPath = `${basePath}/auth/login`;
const deletePath = `${basePath}/single/delete`;

const passwordValidatorRegExp = new RegExp('^[a-zA-Z0-9_-]+$');
const maxUsernameLength = 38;
const maxPasswordLength = 128;

export const UserService = {};

UserService.registerUser = function (username, password) {
	const usernameError = this.validateUsername(username);
	if (usernameError) return Promise.reject({ field: 'username', message: usernameError });

	const passwordError = this.validatePassword(password);
	if (passwordError) return Promise.reject({ field: 'password', message: passwordError });

	return networkService.post(`${registerPath}`, { username, password })
			.catch(error => {
				if (error.statusCode) {
					return Promise.reject({
						message: error.error,
						field: error.data.field,
					});
				}
				//todo handle network errors
				console.warn(error);
				throw error;
			});
};

UserService.checkUsernameAvailability = function (username) {
	return networkService
			.get(`${isAvailablePath}/${username}`);
};

UserService.login = function (username, password) {
	return networkService.post(`${loginPath}`, { username, password })
			.then((data) => {
				return data.jwt;
			})
			.catch(error => Promise.reject('Invalid username or password!'));
};

UserService.delete = function () {
	return networkService.delete(`${deletePath}`);
};

UserService.validateUsername = function (username) {
	if (!username) {
		return 'Username cannot be empty!';
	}

	if (!passwordValidatorRegExp.test(username)) {
		return 'Username can only contain letters, numbers, - and _';
	}

	if (username.length > maxUsernameLength) {
		return `Username cannot be longer than ${maxUsernameLength} characters!`;
	}
};

UserService.validatePassword = function (password) {
	if (!password) {
		return 'Password cannot be empty!';
	}
	if (password.length > maxPasswordLength) {
		return `Password cannot be longer than ${maxPasswordLength} characters!`;
	}
};