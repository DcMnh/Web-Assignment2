package cst8218.jeffin.slider;

import cst8218.jeffin.slider.entity.Slider;
import cst8218.jeffin.slider.util.JsfUtil;
import cst8218.jeffin.slider.util.PaginationHelper;
import jakarta.annotation.PostConstruct;

import java.io.Serializable;
import java.util.ResourceBundle;
import jakarta.annotation.Resource;
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.faces.model.SelectItem;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.UserTransaction;
import java.util.Locale;

/**
 * The SliderController class manages the operations related to the Slider entity,
 * including listing, creating, editing, and deleting sliders. It uses JSF (JavaServer Faces)
 * to interact with the user interface and perform database operations.
 * This class is responsible for handling the UI logic for the Slider entity, pagination, 
 * and data manipulation, such as creating and updating Slider records.
 * 
 * The controller is session-scoped, meaning it is tied to a user's session.
 * 
 * @author User
 */
@Named("sliderController")
@SessionScoped
public class SliderController implements Serializable {

    /**
     * Injected resources for transaction management and entity manager factory
     */
    @Resource
    private UserTransaction utx = null;
    
    /**
     * Injected resources for transaction management and entity manager factory
     */
    @PersistenceUnit(unitName = "my_persistence_unit")
    private EntityManagerFactory emf = null;

    /**
     * Current Slider entity being managed by the controller
     */
    private Slider current;
    
    /**
     * Current Slider entity being managed by the controller
     */
    private DataModel items = null;
    
    /**
     * Current Slider entity being managed by the controller
     */
    private SliderJpaController jpaController = null;
    
    /**
     * Current Slider entity being managed by the controller
     */
    private PaginationHelper pagination;
    
    /**
     * Current Slider entity being managed by the controller
     */
    private int selectedItemIndex;

    /**
     * Locale for internationalization and language settings
     */
    private Locale locale;

    /**
     * Locale for internationalization and language settings
     */
    public SliderController() {
    }

    /**
     * Initializes the controller by setting the current locale from the FacesContext.
     * This method is called automatically after the constructor due to the @PostConstruct annotation.
     */
    @PostConstruct
    public void init() {
        locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
    }

    /**
     * Retrieves the current locale.
     *
     * @return the current Locale object
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Retrieves the language of the current locale.
     *
     * @return the language code of the current locale
     */
    public String getLanguage() {
        return locale.getLanguage();
    }

    /**
     * Sets the language for the current locale.
     * 
     * @param language the language code to set
     */
    public void setLanguage(String language) {
        locale = new Locale(language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }

    /**
     * Retrieves the selected Slider entity, or creates a new one if none is selected.
     *
     * @return the current Slider object
     */
    public Slider getSelected() {
        if (current == null) {
            current = new Slider();
            selectedItemIndex = -1;
        }
        return current;
    }

    /**
     * Returns the JPA controller used to perform database operations on the Slider entity.
     *
     * @return the SliderJpaController instance
     */
    private SliderJpaController getJpaController() {
        if (jpaController == null) {
            jpaController = new SliderJpaController(utx, emf);
        }
        return jpaController;
    }

    /**
     * Retrieves the pagination helper used to manage the pagination of Slider entities.
     *
     * @return the PaginationHelper instance
     */
    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getJpaController().getSliderCount();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getJpaController().findSliderEntities(getPageSize(), getPageFirstItem()));
                }
            };
        }
        return pagination;
    }

    /**
     * Prepares the list view by recreating the data model.
     *
     * @return the navigation outcome string for the list view
     */
    public String prepareList() {
        recreateModel();
        return "List";
    }

    /**
     * Prepares the view for a selected Slider entity.
     *
     * @return the navigation outcome string for the view
     */
    public String prepareView() {
        current = (Slider) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    /**
     * Prepares the creation view by resetting the current Slider entity.
     *
     * @return the navigation outcome string for the create view
     */
    public String prepareCreate() {
        current = new Slider();
        selectedItemIndex = -1;
        return "Create";
    }

    /**
     * Creates a new Slider entity and persists it in the database.
     *
     * @return the navigation outcome string after creation (either create or null)
     */
    public String create() {
        try {
            getJpaController().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SliderCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    /**
     * Prepares the edit view for a selected Slider entity.
     *
     * @return the navigation outcome string for the edit view
     */
    public String prepareEdit() {
        current = (Slider) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    /**
     * Updates an existing Slider entity and persists the changes in the database.
     *
     * @return the navigation outcome string after update (either view or null)
     */
    public String update() {
        try {
            getJpaController().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SliderUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    /**
     * Deletes the selected Slider entity from the database.
     *
     * @return the navigation outcome string for the list view after deletion
     */
    public String destroy() {
        current = (Slider) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    /**
     * Deletes the selected Slider entity and updates the view accordingly.
     *
     * @return the navigation outcome string after deletion (either view or list)
     */
    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // All items were removed, go back to list
            recreateModel();
            return "List";
        }
    }

    /**
     * Helper method to perform the deletion of the selected Slider entity.
     */
    private void performDestroy() {
        try {
            getJpaController().destroy(current.getId());
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SliderDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    /**
     * Updates the current Slider entity based on the selected index and pagination.
     */
    private void updateCurrentItem() {
        int count = getJpaController().getSliderCount();
        if (selectedItemIndex >= count) {
            selectedItemIndex = count - 1;
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getJpaController().findSliderEntities(1, selectedItemIndex).get(0);
        }
    }

    /**
     * Retrieves the data model for the current page of sliders.
     *
     * @return the DataModel of sliders
     */
    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    /**
     * Helper method to recreate the data model for pagination.
     */
    private void recreateModel() {
        items = null;
    }

    /**
     * Helper method to recreate the pagination object.
     */
    private void recreatePagination() {
        pagination = null;
    }

    /**
     * Navigates to the next page of the slider list.
     *
     * @return the navigation outcome string for the list view
     */
    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    /**
     * Navigates to the previous page of the slider list.
     *
     * @return the navigation outcome string for the list view
     */
    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    /**
     * Retrieves the available Slider entities for selection in a multi-selection list.
     *
     * @return an array of SelectItem objects representing the available sliders
     */
    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(getJpaController().findSliderEntities(), false);
    }

    /**
     * Retrieves the available Slider entities for selection in a single-selection list.
     *
     * @return an array of SelectItem objects representing the available sliders
     */
    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(getJpaController().findSliderEntities(), true);
    }

    /**
     * The Converter for Slider entities, used for JSF to convert between String and Slider objects.
     */
    @FacesConverter(forClass = Slider.class)
    public static class SliderControllerConverter implements Converter {
        
        /**
         * Default constructor
         */
        public SliderControllerConverter(){
            
        }

        /**
         * Converts a string value to a Slider entity.
         *
         * @param facesContext the FacesContext
         * @param component the UIComponent
         * @param value the value to convert
         * @return the corresponding Slider entity
         */
        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SliderController controller = (SliderController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "sliderController");
            return controller.getJpaController().findSlider(getKey(value));
        }

        /**
         * Converts a String value to a Long key for a Slider entity.
         *
         * @param value the String value to convert
         * @return the corresponding Long key
         */
        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        /**
         * Converts a Long key to a string representation.
         *
         * @param value the Long value to convert
         * @return the String representation of the key
         */
        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        /**
         * Converts a Slider entity to its String representation (the key).
         *
         * @param facesContext the FacesContext
         * @param component the UIComponent
         * @param object the Slider object
         * @return the String representation of the Slider
         */
        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Slider) {
                Slider o = (Slider) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Slider.class.getName());
            }
        }
    }
}
