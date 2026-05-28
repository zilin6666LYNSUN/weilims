import { listAllUnitmeasure } from "@/api/mes/md/unitmeasure";

const globalData = {
    state: {
        unitmeasureList: []
    },
    mutations: {
        setUnitmeasureList(state, data) {
            state.unitmeasureList = data
        }
    },
    actions: {
        getUnitmeasureList({ commit }) {
            return new Promise((resolve, reject) => {
                listAllUnitmeasure().then(response => {
                    commit('setUnitmeasureList', response.data);
                    resolve(response.data)
                }).catch(error => {
                    reject(error)
                })
            })
        }
    }
}

export default globalData