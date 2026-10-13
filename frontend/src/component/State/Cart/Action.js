import { api } from "../../config/api"
import { ADD_ITEM_TO_CART_FAILURE, ADD_ITEM_TO_CART_REQUEST, ADD_ITEM_TO_CART_SUCCESS, FIND_CART_FAILURE, FIND_CART_REQUEST, FIND_CART_SUCCESS, REMOVE_CART_ITEM_FAILURE, REMOVE_CART_ITEM_REQUEST, REMOVE_CART_ITEM_SUCCESS, UPDATE_CART_ITEM_FAILURE, UPDATE_CART_ITEM_REQUEST, UPDATE_CART_ITEM_SUCCESS } from "./ActionType"

export const findCart = (token) => {
    return async (dispatch) => {
        dispatch({ type: FIND_CART_REQUEST });
        try {
            const res = await api.get(`/cart`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            dispatch({ type: FIND_CART_SUCCESS, payload: res.data });
        } catch (error) {
            dispatch({ type: FIND_CART_FAILURE, payload: error })
        }
    }
}



export const addItemToCart = (reqData) => {
    return async (dispatch) => {
        dispatch({ type: ADD_ITEM_TO_CART_REQUEST });
        try {
            const { data } = await api.post(`/cart/items`, reqData.cartItem, {
                headers: {
                    Authorization: `Bearer ${reqData.token}`,
                    "Content-Type": "application/json"
                },
            });
            dispatch({ type: ADD_ITEM_TO_CART_SUCCESS, payload: data });
        } catch (error) {
            console.log("error", error)
            dispatch({ type: ADD_ITEM_TO_CART_FAILURE, payload: error })
        }
    }
}

export const updateCartItem = (reqData) => {
    return async (dispatch) => {
        dispatch({ type: UPDATE_CART_ITEM_REQUEST });
        try {
            const { data } = await api.put(`/cart/items`, reqData.data, {
                headers: {
                    Authorization: `Bearer ${reqData.jwt}`,
                    "Content-Type": "application/json"
                },
            });
            dispatch({ type: UPDATE_CART_ITEM_SUCCESS, payload: data });
        } catch (error) {
            console.log("error", error)
            dispatch({ type: UPDATE_CART_ITEM_FAILURE, payload: error.message })
        }
    }
}

export const removeCartItem = ({ cartItemId, jwt }) => {
    return async (dispatch) => {
        dispatch({ type: REMOVE_CART_ITEM_REQUEST });
        try {
            const { data } = await api.delete(`/cart/items/${cartItemId}`, {
                headers: {
                    Authorization: `Bearer ${jwt}`,
                },
            });
            dispatch({ type: REMOVE_CART_ITEM_SUCCESS, payload: cartItemId });
        } catch (error) {
            console.log("error", error)
            dispatch({ type: REMOVE_CART_ITEM_FAILURE, payload: error.message })
        }
    }
}


