package com.novoda.pianohero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import static android.view.View.GONE;

public class AndroidThingsActivity extends AppCompatActivity implements GameMvp.View {

    private GamePresenter gamePresenter;
    private GameScreen gameView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("!!!", "I'm running");
        setContentView(R.layout.activity_android_things);

        SimplePitchNotationFormatter simplePitchNotationFormatter = new SimplePitchNotationFormatter();
        Piano piano = createPiano();
        GameModel gameModel = new GameModel(new SongSequenceFactory(), simplePitchNotationFormatter, piano);
        gameView = (GameScreen) findViewById(R.id.game_screen);
        gamePresenter = new GamePresenter(gameModel, this);

        gamePresenter.onCreate();
    }

    private Piano createPiano() {
        C4ToB5ViewPiano virtualPianoView = (C4ToB5ViewPiano) findViewById(R.id.piano_view);
        if (isThingsDevice()) {
            virtualPianoView.setVisibility(GONE);
            return new CompositePiano(new KeyStationMini32Piano(this));
        } else {
            virtualPianoView.setVisibility(View.VISIBLE);
            return new CompositePiano(virtualPianoView, new KeyStationMini32Piano(this));
        }
    }

    public boolean isThingsDevice() {
        return getPackageManager().hasSystemFeature("android.hardware.type.embedded");
        // TODO once targeting 'O' use constant
        // PackageManager.FEATURE_EMBEDDED
    }

    @Override
    protected void onResume() {
        super.onResume();
        gamePresenter.onResume();
    }

    @Override
    public void showRound(RoundViewModel viewModel) {
        gameView.showSuccess(viewModel);
    }

    @Override
    public void showError(RoundViewModel viewModel) {
        gameView.showError(viewModel);
    }

    @Override
    public void showGameComplete(GameOverViewModel viewModel) {
        gameView.showGameComplete(viewModel);
    }

    @Override
    protected void onPause() {
        gamePresenter.onPause();
        super.onPause();
    }
}
