import { _API_URL, api } from "../../config/api";
import { CREATE_INGREDIENT_CATEGORY_SUCCESS, CREATE_INGREDIENT_SUCCESS, GET_INGREDIENT_CATEGORY_SUCCESS, GET_INGREDIENTS, UPDATE_STOCK } from "./ActionType";

export const getIngredientsOfRestaurant = ({ id, jwt }) => {
    return async (dispatch) => {
        try {
            const res = await api.get(
                `/admin/ingredients/restaurant/${id}`,
                {
                    headers: {
                        Authorization: `Bearer ${jwt}`
                    },
                }
            );
            dispatch({ type: GET_INGREDIENTS, payload: res.data })
        } catch (error) {
            console.log("error", error);

        }
    }
}





export const getIngredientCategory = ({ id, jwt }) => {
    return async (dispatch) => {
        try {
            const res = await api.get(
                `/api/admin/ingredients/restaurant/${id}/category`,
                {
                    headers: {
                        Authorization: `Bearer ${jwt}`
                    },
                }
            );
            dispatch({ type: GET_INGREDIENT_CATEGORY_SUCCESS, payload: res.data })
        } catch (error) {
            console.log("error", error);

        }
    }
}

export const updateStockOfIngredient = ({ id, jwt }) => {
    return async (dispatch) => {
        try {
            const { data } = await api.put(
                `/api/admin/ingredients/restaurant/${id}/stock`, {},
                {
                    headers: {
                        Authorization: `Bearer ${jwt}`
                    },
                }
            );
            dispatch({ type: UPDATE_STOCK, payload: data })
        } catch (error) {
            console.log("error", error);

        }
    }
}