
import * as actionTypes from "./ActionType"


const initialState = {
    menuItems: [],
    loading: false,
    error: null,
    search: [],
    message: null
};

const menuItemReducer = (state = initialState, action) => {
    switch (action.type) {
        case actionTypes.GET_MENU_ITEMS_BY_RESTAURANT_ID_REQUEST:
        case actionTypes.UPDATE_MENU_ITEMS_AVAILABILITY_REQUEST:
            return {
                ...state,
                loading: true,
                error: null,
                message: null
            };
        case actionTypes.GET_MENU_ITEMS_BY_RESTAURANT_ID_SUCCESS:
            return {
                ...state,
                loading: false,
                menuItems: Array.isArray(action.payload) ? action.payload : []
            };
        case actionTypes.UPDATE_MENU_ITEMS_AVAILABILITY_SUCCESS:
            console.log("updated items id", action.payload.id)
            return {
                ...state,
                loading: false,
                menuItems: state.menuItems.map(
                    (menuItem) => menuItem.id === action.payload.id ?
                        action.payload : menuItem
                ),
            };

        case actionTypes.GET_MENU_ITEMS_BY_RESTAURANT_ID_FAILURE:
            return {
                ...state,
                menuItems: [],
                loading: false,
                error: action.payload,
                message: null
            };
        default:
            return state;
    }
};


export default menuItemReducer;






