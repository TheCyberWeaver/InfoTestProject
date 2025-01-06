package io.github.infotest;

//ChatGPT

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;

public class StartScreen implements Screen {

    private final Main game;   // 引用主Game，用于切换Screen或访问其他资源
    private Stage stage;           // 场景，用于摆放UI控件
    private TextField nameTextField;     // 姓名输入框
    private SelectBox<String> roleSelectBox; // 职业选择下拉框
    private SelectBox<String> serverSelectBox;
    private CheckBox devModeCheckBox;
    private Viewport viewport;

    // 构造函数，传入主游戏对象
    public StartScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        System.out.println("[StartScreen] StartScreen started");
        // 初始化场景
        viewport = new ScreenViewport();
        stage = new Stage(viewport);
        // 设置输入处理给 stage，这样我们可以捕捉UI事件
        Gdx.input.setInputProcessor(stage);

        // 创建一个皮肤（示例使用内置的皮肤，如果需要漂亮一点，可以用自定义皮肤）
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        // 创建标题Label
        Label titleLabel = new Label("Select Your Name and Your Role", skin);
        titleLabel.setColor(Color.WHITE);
        titleLabel.setFontScale(1.2f); // 放大字体

        // 创建文本框，用于输入用户名
        nameTextField = new TextField("", skin);
        nameTextField.setMessageText("Enter Your Name"); // 提示文字
        nameTextField.setMaxLength(20); // 设定最大输入长度，防止太长
        nameTextField.setTextFieldListener((textField, c) -> {
            // 这里可以监听实时输入，比如检查敏感字符等
        });

        // 创建SelectBox，让玩家选择职业
        roleSelectBox = new SelectBox<>(skin);
        roleSelectBox.setItems("Healer", "Assassin", "Defender", "Mage"); // 设置选项
        roleSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // 可以在这里监听SelectBox切换
                String selectedRole = roleSelectBox.getSelected();
                //System.out.println("Selected:" + selectedRole);
            }
        });

        serverSelectBox = new SelectBox<>(skin);
        serverSelectBox.setItems("Thomas' Server", "Local Server"); // 设置选项
        serverSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {


            }
        });

        devModeCheckBox = new CheckBox("Dev Mode", skin);
        devModeCheckBox.setChecked(true);
        devModeCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.isDevelopmentMode=devModeCheckBox.isChecked();
            }
        });

        // 创建“开始游戏”按钮
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startGame();

            }
        });

        // 使用Table来安排布局
        Table table = new Table();
        table.setFillParent(true); // 填满整个屏幕
        table.defaults().pad(10);  // 设置每个控件之间的间隔

        // 依次添加控件到表格
        table.add(titleLabel).colspan(2).row();   // 占据两列，并换行
        table.add(new Label("Name:", skin));
        table.add(nameTextField).width(200).row();
        table.add(new Label("Role:", skin));
        table.add(roleSelectBox).width(150).row();
        table.add(new Label("Server:", skin));
        table.add(serverSelectBox).width(150).row();
        table.add(startButton).colspan(2);
        table.add(devModeCheckBox).width(200).row();


        // 把Table添加到Stage
        stage.addActor(table);
    }
    public void startGame(){

        String playerName = nameTextField.getText();
        String selectedRole = roleSelectBox.getSelected();
        String selectedServer = serverSelectBox.getSelected();
        // Validation
        if (playerName == null || playerName.trim().isEmpty()) {
            System.out.println("[StartScreen WARNING]: Name cannot be empty!");
            return;
        }

        String selectedServerUrl="";
        switch (selectedServer){
            case "Thomas' Server":
                selectedServerUrl="http://www.thomas-hub.com:9595";
                break;
            case "Local Server":
                selectedServerUrl="http://localhost:9595";
                break;
            default:
                selectedServerUrl="http://www.thomas-hub.com:9595";
                break;
        }
        // Log
        System.out.println("-------Init Setup-------");
        System.out.println("User Name:" + playerName);
        System.out.println("Your Role:" + selectedRole);
        System.out.println("Your Server:" + selectedServerUrl);
        System.out.println("-------Init Setup-------");

        game.startGame(playerName, selectedRole,selectedServerUrl);
    }
    @Override
    public void render(float delta) {
        //System.out.println("render");
        // clear screen
        Gdx.gl.glClearColor(0.439f, 0.5f, 0.5625f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();

        // draw stage
        stage.act(delta);
        stage.draw();
    }
    public void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            startGame();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        // 当切换Screen时，可以手动移除输入处理
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
