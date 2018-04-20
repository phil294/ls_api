import storageService from '@/services/storage-service';
import { SESSION_LOGIN_CREDENTIALS, SESSION_LOGOUT, HIDE_LOGIN_MODAL } from './actions';

export default {
    namespaced: true,
    state: {
        username: storageService.getUsername(),
        email: storageService.getEmail(),
    },
    getters: {
        isLoggedIn(state) {
            return state.username !== null;
        },
    },
    mutations: {
        setUsername(state, username) {
            state.username = username;
        },
        setEmail(state, email) {
            state.email = email;
        },
    },
    actions: {
        async [SESSION_LOGIN_CREDENTIALS]({ commit, dispatch }) {
            /* eslint-disable */
            await new Promise(((resolve, reject) => { // todo
                setTimeout(() => {
                    const session = {
                        username: 'Dummyuser',
                        email: 'user@example.com',
                    };
                    commit('setUsername', session.username);
                    commit('setEmail', session.email);
                    // commit token... // using httpOnly-cookie instead
                    storageService.setUsername(session.username);
                    storageService.setEmail(session.email);
                    dispatch(HIDE_LOGIN_MODAL, null, { root: true });
                    resolve();
                }, 1000);
                // reject('nö');
            }));
        },
        [SESSION_LOGOUT]({ commit }) {
            commit('setUsername', null);
            commit('setEmail', null);
            storageService.setUsername(null);
            storageService.setEmail(null);
        },
    },
};

