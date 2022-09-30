package com.git.must11.plugin.panel;

import com.fr.base.Parameter;
import com.fr.design.data.tabledata.tabledatapane.AbstractTableDataPane;
import com.fr.design.gui.icombobox.UIComboBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.itableeditorpane.ParameterTableModel;
import com.fr.design.gui.itableeditorpane.UITableEditAction;
import com.fr.design.gui.itableeditorpane.UITableEditorPane;
import com.fr.design.i18n.Toolkit;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.utils.ParameterUtils;
import com.fr.general.IOUtils;
import com.fr.stable.ArrayUtils;
import com.fr.stable.ParameterProvider;
import com.git.must11.plugin.UrlJsonTableData;
import com.git.must11.plugin.model.ColumnSet;
import com.git.must11.plugin.model.ColumnSetModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class UrlJsonPanel extends AbstractTableDataPane<UrlJsonTableData> {

    private UrlJsonTableData jsonTableData;
    private UIComboBox methodTypeComboBox;
    private TextField urlTextField;
    private TextField success;
    private TextField successFlag;
    private JsonContentPanel headPane;
    private JsonContentPanel bodyPane;
    private final Dimension TEXT_DIMENSION=  new Dimension(250, 20);
    private UITableEditorPane<ColumnSet> uiTableEditorPane;
    private UITableEditorPane<ParameterProvider> editorPane;
    @Override
    public void populateBean(UrlJsonTableData urlJsonTableData) {
        this.jsonTableData=urlJsonTableData;
        this.urlTextField.setText(urlJsonTableData.getUrl());
        this.methodTypeComboBox.setSelectedItem(urlJsonTableData.getMethod());
        this.headPane.populate(urlJsonTableData.getRequestHead());
        this.bodyPane.populate(urlJsonTableData.getRequestBody());
        this.successFlag.setText(urlJsonTableData.getReposeSuccessFlag());
        this.success.setText(urlJsonTableData.getResponseSuccess());
        this.editorPane.populate(urlJsonTableData.getParams());
        String[] columnNameList=  urlJsonTableData.getColumnNameList();
       String[] propertyList= urlJsonTableData.getPropertyList();
        ColumnSet[] list= new ColumnSet[columnNameList.length];
        for(int i=0;i<columnNameList.length;i++) {
            ColumnSet columnSet= new ColumnSet(columnNameList[i],propertyList[i]);
            list[i]=columnSet;
        }
        this.uiTableEditorPane.populate(list);
    }
    public UrlJsonPanel(){

        this.setLayout(new BorderLayout(34, 0));
        this.add(request(),"West");
        this.add(rightPanel(),"East");
    }
    private JPanel request(){
        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BorderLayout(10, 0));
        westPanel.setPreferredSize(new Dimension(522, 200));
        westPanel.setBorder(BorderFactory.createTitledBorder(Toolkit.i18nText("Plugin-Request_set")));
        double var1 = -2.0;
        double[] var3 = new double[]{var1, var1, var1, var1};
        double[] var4 = new double[]{var1, var1,var1};
        String[] methodType = new String[]{"POST", "GET"};
        this.methodTypeComboBox = new UIComboBox(methodType);
        this.urlTextField = new TextField();
        this.urlTextField.setPreferredSize(TEXT_DIMENSION);
        Component[][] components = new Component[][]{
                {new UILabel(Toolkit.i18nText("Plugin-Request_Method") + ":"),  this.methodTypeComboBox, this.urlTextField}
        };
        JPanel jPanel = TableLayoutHelper.createTableLayoutPane(components, var3, var4);
        Box box = new Box(1);
        this.headPane = new JsonContentPanel();
        this.headPane.setTitle(Toolkit.i18nText("Plugin-Request_head"));
        this.bodyPane = new JsonContentPanel();
        this.bodyPane.setTitle(Toolkit.i18nText("Plugin-Request_body"));
        box.add(jPanel);
        box.add(this.headPane);
        box.add(this.bodyPane);
        westPanel.add(box,"Center");
        return westPanel;
    }
    private JPanel responsePanel(){
        JPanel  response = new JPanel();
        response.setLayout(new BorderLayout(10, 0));
        response.setPreferredSize(new Dimension(350, 200));
        response.setBorder(BorderFactory.createTitledBorder(Toolkit.i18nText("Plugin-Response_set")));
        double var1 = -2.0;
        double[] var3 = new double[]{var1, var1, var1, var1};
        double[] var4 = new double[]{var1, var1};
        this.success =new TextField();
        this.success.setPreferredSize(TEXT_DIMENSION);
        this.successFlag = new TextField();
        this.successFlag.setPreferredSize(TEXT_DIMENSION);
        Component[][] components = new Component[][]{
                {new UILabel(Toolkit.i18nText("Plugin-Fail") + ":"),  this.successFlag},
                {new UILabel(Toolkit.i18nText("Plugin-Success_Object") + ":"),  this.success}
        };
        JPanel jPanel = TableLayoutHelper.createTableLayoutPane(components, var3, var4);
        response.add(jPanel,"North");
        this.uiTableEditorPane = new UITableEditorPane(new ColumnSetModel());

        JPanel editorPanel = new JPanel();
        editorPanel.setPreferredSize(new Dimension(-1, 150));
        editorPanel.setLayout(new BorderLayout());
        editorPanel.add(this.uiTableEditorPane, "Center");
        response.add(editorPanel,"Center");
        return response;
    }
    private JPanel rightPanel(){
        JPanel  rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout(10, 0));
        rightPanel.setPreferredSize(new Dimension(350, 460));
        rightPanel.add(responsePanel(),"Center");
        rightPanel.add(initSouthPanel(),"South");
        return rightPanel;
    }
    private JPanel initSouthPanel() {
        JPanel var1 = new JPanel();
        var1.setPreferredSize(new Dimension(-1, 150));
        var1.setLayout(new BorderLayout());
        ParameterTableModel parameterTableModel = new ParameterTableModel() {
            public UITableEditAction[] createAction() {
                return ArrayUtils.add(super.createDBTableAction(), UrlJsonPanel.this.new RefreshAction());
            }
        };
        this.editorPane = new UITableEditorPane(parameterTableModel);
        var1.add(this.editorPane, "Center");
        return var1;
    }
    @Override
    public UrlJsonTableData updateBean() {
        this.jsonTableData= new UrlJsonTableData();
        this.jsonTableData.setUrl(this.urlTextField.getText());
        this.jsonTableData.setMethod((String) this.methodTypeComboBox.getSelectedItem());
        this.jsonTableData.setRequestHead(this.headPane.update());
        this.jsonTableData.setRequestBody(this.bodyPane.update());
        this.jsonTableData.setReposeSuccessFlag(this.successFlag.getText());
        this.jsonTableData.setResponseSuccess(this.success.getText());
        List<ColumnSet> list =this.uiTableEditorPane.update();
        if(list.size()>0) {
            String[] columnNameList=new String[list.size()];
            String[] propertyList=new String[list.size()];
            for (int i=0;i< list.size();i++) {
                columnNameList[i]= list.get(i).getColumnName();
                propertyList[i]= list.get(i).getProperty();
            }
            this.jsonTableData.setColumnNameList(columnNameList);
            this.jsonTableData.setPropertyList(propertyList);
        }

        return jsonTableData;
    }

    @Override
    protected String title4PopupWindow() {
        return Toolkit.i18nText("Plugin-Name");
    }

    protected class RefreshAction extends UITableEditAction {
        public RefreshAction() {
            this.setName(Toolkit.i18nText("Plugin-Refresh"));
            this.setSmallIcon(IOUtils.readIcon("/com/fr/design/images/control/refresh.png"));
        }

        public void actionPerformed(ActionEvent var1) {
            UrlJsonPanel.this.refreshParameters();
        }

        public void checkEnabled() {
        }
    }

    private void refreshParameters() {
        String[] strings =new String[]{this.urlTextField.getText(),this.headPane.update(),this.bodyPane.update()};
        List<ParameterProvider> parameterProviderList = this.editorPane.update();
        Parameter[] parameters = parameterProviderList == null ? new Parameter[0] : parameterProviderList.toArray(new Parameter[0]);
        this.editorPane.populate(ParameterUtils.analyzeAndUnionParameters(strings, parameters));
    }
}
