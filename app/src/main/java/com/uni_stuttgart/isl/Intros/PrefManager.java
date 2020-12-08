package com.uni_stuttgart.isl.Intros;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Borg on 08/08/16.
 */
public class PrefManager {
    // Shared preferences file name
    private static final String PREF_NAME = "my-welcome";
    private static final String IS_MAIN_LAST_ACTIVITY = "isMainLastActivity";
    private static final String IS_INTER_LAST_ACTIVITY = "isInterLastActivity";
    private static final String IS_INTEG_LAST_ACTIVITY = "isIntegLastActivity";
    private static final String IS_RIEM_LAST_ACTIVITY = "isRiemLastActivity";
    private static final String IS_SPLIT_LAST_ACTIVITY = "isSplitLastActivity";
    private static final String IS_ZERO_LAST_ACTIVITY = "isZeroLastActivity";
    private static final String IS_GAUS_LAST_ACTIVITY = "isGausLastActivity";
    private static final String IS_ELIMINATELGS_LAST_ACTIVITY = "isEliminateLGSLastActivity";
    private static final String IS_EIGENWERTE_LAST_ACTIVITY = "isEigenwerteLastActivity";

    private static final String IS_GOING_BACK = "isGoingBack";

    private static final String IS_SPLITTINGSOLVER_FIRST_TIME_LAUNCH = "IsSplittingSolverFirstTimeLaunch";
    private static final String IS_INTERPOLATION_FIRST_TIME_LAUNCH = "IsInterpolationFirstTimeLaunch";
    private static final String IS_INTEGRATION_FIRST_TIME_LAUNCH = "IsIntegrationFirstTimeLaunch";
    private static final String IS_RIEMANN_FIRST_TIME_LAUNCH = "IsRiemannFirstTimeLaunch";
    private static final String IS_ZEROFINDING_FIRST_TIME_LAUNCH = "IsZerofindingFirstTimeLaunch";
    private static final String IS_GAUSSIAN_FIRST_TIME_LAUNCH = "IsGaussianFirstTimeLaunch";
    private static final String IS_ELIMINATELGS_FIRST_TIME_LAUNCH = "IsEliminateLGSFirstTimeLaunch";
    private static final String IS_EIGENWERTE_FIRST_TIME_LAUNCH = "IsEigenwerteFirstTimeLaunch";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String REOPEN_APP_INTRO = "reopenAppIntro";

    private static final String REOPEN_INTERPOLATION_INTRO = "reopenInterpolationIntro";
    private static final String REOPEN_SPLITTINGSOLVER_INTRO = "reopenSpittingSolverIntro";
    private static final String REOPEN_INTEGRATION_INTRO = "reopenIntegrationIntro";
    private static final String REOPEN_RIEMANN_INTRO = "reopenRiemannIntro";
    private static final String REOPEN_ZEROFINDING_INTRO = "reopenZerofindingIntro";
    private static final String REOPEN_GAUSSIAN_INTRO = "reopenGaussianIntro";
    private static final String REOPEN_ELIMINATELGS_INTRO = "reopenEliminateLGSIntro";
    private static final String REOPEN_EIGENWERTE_INTRO = "reopenEigenwerteIntro";

    private static final String IS_INTERPOLTION_SAVED = "isInterpolationSaved";
    private static final String IS_INTEGRATION_SAVED = "isIntegrationSaved";
    private static final String IS_RIEMANN_SAVED = "isRiemannSaved";
    private static final String IS_ZEROFINDING_SAVED = "isZerofindingSaved";
    private static final String IS_GAUSSIAN_SAVED = "isGaussianSaved";
    private static final String IS_ELIMINATELGS_SAVED = "isEliminateLGSSaved";
    private static final String IS_EIGENWERTE_SAVED = "isEigenwerteSaved";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    // shared pref mode
    int PRIVATE_MODE = 0;
    private boolean integrationSaved;


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    public void setFirstTimeInterpolationLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_INTERPOLATION_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    public void setFirstTimeIntegrationLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_INTEGRATION_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    public void setFirstTimeRiemannLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_RIEMANN_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    public void setFirstTimeSplittingSolverLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_SPLITTINGSOLVER_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    public void setFirstTimeZerofindingLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_ZEROFINDING_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    public void setFirstTimeGaussianLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_GAUSSIAN_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    public void setFirstTimeEliminateLGSLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_ELIMINATELGS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }
    public void setFirstTimeEigenwerteLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_EIGENWERTE_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
    public boolean isInterpolationFirstTimeLaunch() {
        return pref.getBoolean(IS_INTERPOLATION_FIRST_TIME_LAUNCH, true);
    }
    public boolean isIntegrationFirstTimeLaunch() {
        return pref.getBoolean(IS_INTEGRATION_FIRST_TIME_LAUNCH, true);
    }
    public boolean isRiemannFirstTimeLaunch() {
        return pref.getBoolean(IS_RIEMANN_FIRST_TIME_LAUNCH, true);
    }
    public boolean isSplittingSolverFirstTimeLaunch() {
        return pref.getBoolean(IS_SPLITTINGSOLVER_FIRST_TIME_LAUNCH, true);
    }
    public boolean isZerofindingFirstTimeLaunch() {
        return pref.getBoolean(IS_ZEROFINDING_FIRST_TIME_LAUNCH, true);
    }
    public boolean isGaussianFirstTimeLaunch() {
        return pref.getBoolean(IS_GAUSSIAN_FIRST_TIME_LAUNCH, true);
    }
    public boolean isEliminateLGSFirstTimeLaunch() {
        return pref.getBoolean(IS_ELIMINATELGS_FIRST_TIME_LAUNCH, true);
    }
    public boolean isEigenwerteFirstTimeLaunch() {
        return pref.getBoolean(IS_EIGENWERTE_FIRST_TIME_LAUNCH, true);
    }

    public boolean isMainLastActivity() {
        return pref.getBoolean(IS_MAIN_LAST_ACTIVITY, true);
    }
    public boolean isInterLastActivity() {
        return pref.getBoolean(IS_INTER_LAST_ACTIVITY, true);
    }
    public boolean isRiemLastActivity() {
        return pref.getBoolean(IS_RIEM_LAST_ACTIVITY, true);
    }
    public boolean isSplitLastActivity() {
        return pref.getBoolean(IS_SPLIT_LAST_ACTIVITY, true);
    }
    public boolean isZeroLastActivity() {
        return pref.getBoolean(IS_ZERO_LAST_ACTIVITY, true);
    }
    public boolean isGausLastActivity() {
        return pref.getBoolean(IS_GAUS_LAST_ACTIVITY, true);
    }
    public boolean isEliminateLGSLastActivity() {
        return pref.getBoolean(IS_ELIMINATELGS_LAST_ACTIVITY, true);
    }
    public boolean isEigenwerteLastActivity() {
        return pref.getBoolean(IS_EIGENWERTE_LAST_ACTIVITY, true);
    }

    public void setIsMainLastActivity(boolean isMainLastActivity) {
        editor.putBoolean(IS_MAIN_LAST_ACTIVITY, isMainLastActivity);
        editor.commit();
    }
    public void setIsInterLastActivity(boolean isInterLastActivity) {
        editor.putBoolean(IS_INTER_LAST_ACTIVITY, isInterLastActivity);
        editor.commit();
    }
    public void setIsIntegLastActivity(boolean isIntegLastActivity) {
        editor.putBoolean(IS_INTEG_LAST_ACTIVITY, isIntegLastActivity);
        editor.commit();
    }
    public void setIsRiemLastActivity(boolean isRiemLastActivity) {
        editor.putBoolean(IS_RIEM_LAST_ACTIVITY, isRiemLastActivity);
        editor.commit();
    }
    public void setIsSplitLastActivity(boolean isSplitLastActivity) {
        editor.putBoolean(IS_SPLIT_LAST_ACTIVITY, isSplitLastActivity);
        editor.commit();
    }
    public void setIsZeroLastActivity(boolean isZeroLastActivity) {
        editor.putBoolean(IS_SPLIT_LAST_ACTIVITY, isZeroLastActivity);
        editor.commit();
    }
    public void setIsGausLastActivity(boolean isGausLastActivity) {
        editor.putBoolean(IS_GAUS_LAST_ACTIVITY, isGausLastActivity);
        editor.commit();
    }
    public void setIsEliminateLGSLastActivity(boolean isGausLastActivity) {
        editor.putBoolean(IS_ELIMINATELGS_LAST_ACTIVITY, isGausLastActivity);
        editor.commit();
    }
    public void setIsEigenwerteLastActivity(boolean isGausLastActivity) {
        editor.putBoolean(IS_EIGENWERTE_LAST_ACTIVITY, isGausLastActivity);
        editor.commit();
    }


    public void setReopenAppIntro(boolean reopenAppIntro) {
        editor.putBoolean(REOPEN_APP_INTRO, reopenAppIntro);
        editor.commit();
    }
    public void setReopenInterpolationIntro(boolean reopenInterpolationIntro) {
        editor.putBoolean(REOPEN_INTERPOLATION_INTRO, reopenInterpolationIntro);
        editor.commit();
    }
    public void setReopenIntegrationIntro(boolean reopenIntegrationIntro) {
        editor.putBoolean(REOPEN_INTERPOLATION_INTRO, reopenIntegrationIntro);
        editor.commit();
    }
    public void setReopenRiemannIntro(boolean reopenRiemannIntro) {
        editor.putBoolean(REOPEN_RIEMANN_INTRO, reopenRiemannIntro);
        editor.commit();
    }
    public void setReopenSplittingSolverIntro(boolean reopenSplittingSolverIntro) {
        editor.putBoolean(REOPEN_SPLITTINGSOLVER_INTRO, reopenSplittingSolverIntro);
        editor.commit();
    }
    public void setReopenZerofindingIntro(boolean reopenZerofindingIntro) {
        editor.putBoolean(REOPEN_ZEROFINDING_INTRO, reopenZerofindingIntro);
        editor.commit();
    }
    public void setReopenGaussianIntro(boolean reopenGaussianIntro) {
        editor.putBoolean(REOPEN_GAUSSIAN_INTRO, reopenGaussianIntro);
        editor.commit();
    }
    public void setReopenEliminateLGSIntro(boolean reopenGaussianIntro) {
        editor.putBoolean(REOPEN_ELIMINATELGS_INTRO, reopenGaussianIntro);
        editor.commit();
    }
    public void setReopenEigenwerteIntro(boolean reopenGaussianIntro) {
        editor.putBoolean(REOPEN_EIGENWERTE_INTRO, reopenGaussianIntro);
        editor.commit();
    }

    public boolean isReopenAppIntro() {
        return pref.getBoolean(REOPEN_APP_INTRO, true);
    }
    public boolean isReopenInterpolationIntro() {
        return pref.getBoolean(REOPEN_INTERPOLATION_INTRO, true);
    }
    public boolean isReopenIntegrationIntro() {
        return pref.getBoolean(REOPEN_INTERPOLATION_INTRO, true);
    }
    public boolean isReopenRiemannIntro() {
        return pref.getBoolean(REOPEN_RIEMANN_INTRO, true);
    }
    public boolean isReopenSplittingSolverIntro() {
        return pref.getBoolean(REOPEN_SPLITTINGSOLVER_INTRO, true);
    }
    public boolean isReopenZerofindingIntro() {
        return pref.getBoolean(REOPEN_ZEROFINDING_INTRO, true);
    }
    public boolean isReopenGaussianIntro() {
        return pref.getBoolean(REOPEN_GAUSSIAN_INTRO, true);
    }
    public boolean isReopenEliminateLGSIntro() {
        return pref.getBoolean(REOPEN_ELIMINATELGS_INTRO, true);
    }
    public boolean isReopenEigenwerteIntro() {
        return pref.getBoolean(REOPEN_EIGENWERTE_INTRO, true);
    }


    public void setIsGoingBack(boolean isGoingBack) {
        editor.putBoolean(IS_GOING_BACK, isGoingBack);
        editor.commit();
    }
    public boolean isGoingBack() {
        return pref.getBoolean(IS_GOING_BACK, true);
    }


    public void setIsInterpolationSaved(boolean isSaved) {
        editor.putBoolean(IS_INTERPOLTION_SAVED, isSaved);
        editor.commit();
    }
    public void setIsIntegrationSaved(boolean isSaved) {
        editor.putBoolean(IS_INTEGRATION_SAVED, isSaved);
        editor.commit();
    }
    public void setIsRiemannSaved(boolean isSaved) {
        editor.putBoolean(IS_RIEMANN_SAVED, isSaved);
        editor.commit();
    }
    public void setIsZerofindingSaved(boolean isSaved) {
        editor.putBoolean(IS_ZEROFINDING_SAVED, isSaved);
        editor.commit();
    }
    public void setIsGaussianSaved(boolean isSaved) {
        editor.putBoolean(IS_GAUSSIAN_SAVED, isSaved);
        editor.commit();
    }
    public void setIsEliminateLGSSaved(boolean isSaved) {
        editor.putBoolean(IS_ELIMINATELGS_SAVED, isSaved);
        editor.commit();
    }
    public void setIsEigenwerteSaved(boolean isSaved) {
        editor.putBoolean(IS_EIGENWERTE_SAVED, isSaved);
        editor.commit();
    }

    public boolean isInterpolationSaved(){
        return pref.getBoolean(IS_INTERPOLTION_SAVED, true);
    }
    public boolean isIntegrationSaved() {
        return pref.getBoolean(IS_INTEGRATION_SAVED, true);
    }
    public boolean isRiemannSaved() {
        return pref.getBoolean(IS_RIEMANN_SAVED, true);
    }
    public boolean isZerofindingSaved() {
        return pref.getBoolean(IS_ZEROFINDING_SAVED, true);
    }
    public boolean isGaussianSaved() {
        return pref.getBoolean(IS_GAUSSIAN_SAVED, true);
    }
    public boolean isEliminateLGSSaved() {
        return pref.getBoolean(IS_ELIMINATELGS_SAVED, true);
    }
    public boolean isEligenwerteSaved() {
        return pref.getBoolean(IS_EIGENWERTE_SAVED, true);
    }
}
