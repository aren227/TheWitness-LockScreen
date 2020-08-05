package com.aren.thewitnesspuzzle.puzzle;

import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.widget.Toast;

import com.aren.thewitnesspuzzle.graphics.Circle;
import com.aren.thewitnesspuzzle.graphics.Rectangle;
import com.aren.thewitnesspuzzle.graphics.Shape;
import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLine;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPoint;
import com.aren.thewitnesspuzzle.puzzle.rules.HexagonDots;
import com.aren.thewitnesspuzzle.puzzle.rules.Rule;
import com.aren.thewitnesspuzzle.puzzle.rules.Square;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPoint;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class GridPuzzle implements Puzzle {

    Game game;

    ArrayList<Shape> staticShapes = new ArrayList<>();
    ArrayList<Shape> dynamicShapes = new ArrayList<>();

    int backgroundColor;
    int pathColor;
    int cursorColor;

    int width, height;
    float pathWidth;

    boolean touching = false;
    Vector3 cursorPosition;
    ArrayList<Vector3> cursorPositionStack;
    StartingPoint currentStartingPoint;
    EndingPoint currentEndingPoint;

    Rule[][] tileRules, hLineRules, vLineRules, cornerRules;
    ArrayList<StartingPoint> startingPoints;
    ArrayList<EndingPoint> endingPoints;
    HashSet<Class<? extends Rule>> appliedRules;

    public GridPuzzle(Game game, int width, int height){
        this.game = game;
        this.width = width;
        this.height = height;

        ColorFactory.setRandomColor(this);

        pathWidth = (float)Math.sqrt(getSceneWidth() - 1) * 0.1f + 0.1f;

        tileRules = new Rule[width][height];
        hLineRules = new Rule[width][height + 1];
        vLineRules = new Rule[width + 1][height];
        cornerRules = new Rule[width + 1][height + 1];
        startingPoints = new ArrayList<>();
        endingPoints = new ArrayList<>();

        appliedRules = new HashSet<>();

        //tileRules[0][0] = new Square(this, 0, 0, Rule.Where.TILE, Color.BLACK);
        //tileRules[1][0] = new Square(this, 1, 0, Rule.Where.TILE, Color.WHITE);
        //hLineRules[0][0] = new BrokenLine(this, 0, 0, Rule.Where.HLINE);
        //hLineRules[2][1] = new BrokenLine(this, 2, 1, Rule.Where.HLINE);
        //vLineRules[2][3] = new BrokenLine(this, 2, 3, Rule.Where.VLINE);
        //vLineRules[0][1] = new HexagonDots(this, 0, 1, Rule.Where.VLINE);
        //cornerRules[2][2] = new HexagonDots(this, 2, 2, Rule.Where.CORNER);

        PuzzleFactory factory = new PuzzleFactory(this);
        factory.generatePuzzle();

        /*
        Solver solver = new Solver(this);
        long start = System.currentTimeMillis();
        Log.i("SOLUTIONS", "" + solver.solve());
        Log.i("MAX_LENGTH", "" + (solver.maxLength + 1));
        Log.i("TIME ELAPSED", (System.currentTimeMillis() - start) + " ms");
        */

        calcStaticShapes();
    }

    //고정된 퍼즐을 미리 그려놓자
    @Override
    public void calcStaticShapes(){
        staticShapes.add(new Circle(new Vector3(0, 0, 0), pathWidth * 0.5f, getPathColor()));
        staticShapes.add(new Circle(new Vector3(width, 0, 0), pathWidth * 0.5f, getPathColor()));
        staticShapes.add(new Circle(new Vector3(0, height, 0), pathWidth * 0.5f, getPathColor()));
        staticShapes.add(new Circle(new Vector3(width, height, 0), pathWidth * 0.5f, getPathColor()));

        for(int i = 0; i <= width; i++){
            staticShapes.add(new Rectangle(new Vector3(i, height / 2f, 0), pathWidth, height, getPathColor()));
        }
        for(int i = 0; i <= height; i++){
            staticShapes.add(new Rectangle(new Vector3(width / 2f, i, 0), width, pathWidth, getPathColor()));
        }

        for(StartingPoint startingPoint : startingPoints){
            staticShapes.add(startingPoint.getShape());
        }

        for(EndingPoint endingPoint : endingPoints){
            staticShapes.add(new Circle(endingPoint.getActualEndPosition(), pathWidth * 0.5f, getPathColor()));
            if(endingPoint.isHorizontal()){
                staticShapes.add(new Rectangle(endingPoint.getActualEndPosition().middle(new Vector3(endingPoint.x, endingPoint.y, 0)), EndingPoint.getLength(this), pathWidth, getPathColor()));
            }
            else{
                staticShapes.add(new Rectangle(endingPoint.getActualEndPosition().middle(new Vector3(endingPoint.x, endingPoint.y, 0)), pathWidth, EndingPoint.getLength(this), getPathColor()));
            }
        }

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height + 1; j++){
                if(hLineRules[i][j] == null) continue;
                staticShapes.add(hLineRules[i][j].getShape());
            }
        }
        for(int i = 0; i < width + 1; i++){
            for(int j = 0; j < height; j++){
                if(vLineRules[i][j] == null) continue;
                staticShapes.add(vLineRules[i][j].getShape());
            }
        }
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                if(tileRules[i][j] == null) continue;
                staticShapes.add(tileRules[i][j].getShape());
            }
        }
        for(int i = 0; i <= width; i++){
            for(int j = 0; j <= height; j++){
                if(cornerRules[i][j] == null) continue;
                staticShapes.add(cornerRules[i][j].getShape());
            }
        }
    }

    //커서 등은 따로 그리자
    @Override
    public void calcDynamicShapes(){
        dynamicShapes.clear();

        if(touching){
            dynamicShapes.add(currentStartingPoint.getShape());

            dynamicShapes.add(new Circle(cursorPosition, pathWidth / 2, getCursorColor()));
            for(Vector3 vec : cursorPositionStack){
                dynamicShapes.add(new Circle(vec, pathWidth / 2, getCursorColor()));
            }

            cursorPositionStack.add(cursorPosition); // For loop convenience
            for(int i = 1; i < cursorPositionStack.size(); i++){
                if(cursorPositionStack.get(i - 1).x == cursorPositionStack.get(i).x) dynamicShapes.add(new Rectangle(new Vector3(cursorPositionStack.get(i).x, (cursorPositionStack.get(i).y + cursorPositionStack.get(i - 1).y) / 2, 0), pathWidth, Math.abs(cursorPositionStack.get(i).y - cursorPositionStack.get(i - 1).y), getCursorColor()));
                else dynamicShapes.add(new Rectangle(new Vector3((cursorPositionStack.get(i).x + cursorPositionStack.get(i - 1).x) / 2, cursorPositionStack.get(i).y, 0), Math.abs(cursorPositionStack.get(i).x - cursorPositionStack.get(i - 1).x), pathWidth, getCursorColor()));
            }
            cursorPositionStack.remove(cursorPositionStack.size() - 1);
        }
    }

    @Override
    public boolean touchEvent(float x, float y, int action){
        if(action == MotionEvent.ACTION_DOWN){
            currentStartingPoint = null;
            for(StartingPoint startingPoint : startingPoints){
                if(Math.sqrt((x - startingPoint.x) * (x - startingPoint.x) + (y - startingPoint.y) * (y - startingPoint.y)) <= StartingPoint.getCircleRadius(this)){
                    currentStartingPoint = startingPoint;
                    touching = true;
                    cursorPosition = new Vector3(startingPoint.x, startingPoint.y, 0);
                    cursorPositionStack = new ArrayList<>();
                    calculateCursorPosition(x, y);

                    game.playSound(Sounds.START_TRACE);

                    break;
                }
            }
        }
        else if(action == MotionEvent.ACTION_MOVE){
            if(touching){
                calculateCursorPosition(x, y);
            }
        }
        else if(action == MotionEvent.ACTION_UP){
            if(touching){
                touching = false;
                currentEndingPoint = null;
                for(EndingPoint endingPoint : endingPoints){
                    if(Math.abs(cursorPosition.x - endingPoint.x) + Math.abs(cursorPosition.y - endingPoint.y) < EndingPoint.getLength(this)){
                        currentEndingPoint = endingPoint;

                        game.playSound(Sounds.FINISH_TRACE);
                        if(validate(cursorPositionStack)){
                            game.playSound(Sounds.SUCCESS);
                            return true;
                        }
                        else{
                            game.playSound(Sounds.FAILURE);
                            return false;
                        }
                    }
                }

                game.playSound(Sounds.ABORT_TRACE);
            }
        }
        return false;
    }

    /*
    TODO: Dirty as hell.
    나중에 퍼즐을 그래프를 기반으로 하는 자료구조로 관리해야 될 것 같다.
     */
    private void calculateCursorPosition(float x, float y){
        if(cursorPositionStack.size() == 0) cursorPositionStack.add(new Vector3(currentStartingPoint.x, currentStartingPoint.y, 0));

        x = Math.max(Math.min(x, width + EndingPoint.getLength(this)), -EndingPoint.getLength(this));
        y = Math.max(Math.min(y, height + EndingPoint.getLength(this)), -EndingPoint.getLength(this));

        //커서가 그리드 밖인 경우
        //x = Math.max(Math.min(x, width), 0);
        //y = Math.max(Math.min(y, height + pathWidth), 0); //도착 지점 고려

        int cx = (int)x;
        int cy = (int)y;

        /*
        //도착 선분에 가장 가까운 경우
        if(x - width + height <= y && -(x - width) + height <= y){
            x = width;
        }
        else{
            y = Math.min(y, height);

            float dx = x - cx;
            float dy = y - cy;
            if(dx >= dy){
                if(1 - dx >= dy){
                    y = cy;
                }
                else{
                    x = cx + 1;
                }
            }
            else{
                if(1 - dx >= dy){
                    x = cx;
                }
                else{
                    y = cy + 1;
                }
            }
        }
        */
        //y = Math.min(y, height);

        float dx = x - cx;
        float dy = y - cy;
        if(dx >= dy){
            if(1 - dx >= dy){
                y = cy;
            }
            else{
                x = cx + 1;
            }
        }
        else{
            if(1 - dx >= dy){
                x = cx;
            }
            else{
                y = cy + 1;
            }
        }

        EndingPoint endingPoint = null;
        for(EndingPoint ep : endingPoints){
            if(ep.isHorizontal()){
                if(ep.y == y && Math.abs(ep.x - x) <= EndingPoint.getLength(this)){
                    endingPoint = ep;
                    break;
                }
            }
            else{
                if(ep.x == x && Math.abs(ep.y - y) <= EndingPoint.getLength(this)){
                    endingPoint = ep;
                    break;
                }
            }
        }

        if(endingPoint == null){
            x = Math.max(Math.min(x, width), 0);
            y = Math.max(Math.min(y, height), 0);
        }

        boolean done = false;

        //이전 꼭짓점으로도 닿을 수 있는가?
        if(cursorPositionStack.size() > 1){
            Vector3 last = cursorPositionStack.get(cursorPositionStack.size() - 2);
            if(Math.abs(last.x - x) + Math.abs(last.y - y) < 1){
                cursorPositionStack.remove(cursorPositionStack.size() - 1);
                cursorPosition = new Vector3(x, y, 0);
                done = true;
            }
        }

        //이번 꼭짓점으로 닿을 수 있는가?
        Vector3 current = cursorPositionStack.get(cursorPositionStack.size() - 1);
        if(Math.abs(current.x - x) + Math.abs(current.y - y) < 1){
            cursorPosition = new Vector3(x, y, 0);
            done = true;
        }

        if(done){
            //이전 터치 좌표는 꼭짓점에 충돌하지 않았지만
            //이번 터치 좌표는 꼭짓점에 충돌한 경우
            //단 충돌 검사할 꼭짓점 중 마지막에 추가된 두 꼭짓점은 제외해야 한다.
            for(int i = 0; i < cursorPositionStack.size() - 2; i++){
                Vector3 vec = cursorPositionStack.get(i);
                if(Math.sqrt((vec.x - x) * (vec.x - x) + (vec.y - y) * (vec.y - y)) < pathWidth){ //r = pathWidth / 2
                    //꼭짓점에서 마지막 커서 위치(=마지막으로 추가된 꼭짓점을 향해도 같은 방향이므로)를 향한 벡터. 길이는 pathWidth
                    Vector3 last = cursorPositionStack.get(cursorPositionStack.size() - 1);
                    Vector3 dir = new Vector3(last.x - vec.x, last.y - vec.y, 0);
                    float length = (float)Math.sqrt(dir.x * dir.x + dir.y * dir.y);
                    dir.x = dir.x / length * pathWidth;
                    dir.y = dir.y / length * pathWidth;

                    x = dir.x + vec.x;
                    y = dir.y + vec.y;
                    cursorPosition = new Vector3(x, y, 0);
                    break;
                }
            }

            //Broken Line
            Vector3 last = cursorPositionStack.get(cursorPositionStack.size() - 1);
            boolean isH = cursorPosition.y == last.y;

            Rule rule = null;
            if(isH) rule = hLineRules[Math.min((int)cursorPosition.x, width - 1)][(int)cursorPosition.y];
            else rule = vLineRules[(int)cursorPosition.x][Math.min((int)cursorPosition.y, height - 1)];

            if(rule instanceof BrokenLine){
                BrokenLine brokenLine = (BrokenLine)rule;

                float dist = Math.abs(last.x - cursorPosition.x) + Math.abs(last.y - cursorPosition.y);
                float maxDist = (1 - brokenLine.getSize()) / 2 - pathWidth / 2;
                if(dist > maxDist){ //충돌했다
                    Vector3 dir = new Vector3(cursorPosition.x - last.x, cursorPosition.y - last.y, 0);
                    float length = (float)Math.sqrt(dir.x * dir.x + dir.y * dir.y);
                    dir.x = dir.x / length * maxDist;
                    dir.y = dir.y / length * maxDist;

                    cursorPosition.x = dir.x + last.x;
                    cursorPosition.y = dir.y + last.y;
                }
            }

            return;
        }

        //새로운 꼭짓점을 추가해야 하는가?
        int[] deltaX = {-1, 0, 1, 0};
        int[] deltaY = {0, 1, 0, -1};
        for(int i = 0; i < 4; i++){
            Vector3 add = new Vector3(current.x + deltaX[i], current.y + deltaY[i], 0);
            if(Math.abs(add.x - x) + Math.abs(add.y - y) < 1){
                //이미 있는 점이라면 추가해선 안된다. 그려진 선은 통과할 수 없기 때문.
                //그리고 delta에 의한 current 주위 4개의 꼭짓점들은 겹치지 않는 마름모 영역을 나태내므로
                //영역에 포함되는 꼭짓점을 찾았다면 더 이상 다른 꼭짓점은 고려할 필요가 없다
                boolean found = false;
                for(Vector3 vec : cursorPositionStack){
                    if(vec.x == add.x && vec.y == add.y){
                        found = true;
                        break;
                    }
                }

                if(found){
                    //겹치는 꼭짓점으로 커서를 projection 한다
                    //꼭짓점에서 마지막 커서 위치를 향한 벡터. 길이는 pathWidth
                    //마지막으로 추가된 꼭짓점을 향해도 같은 방향이므로 그걸 사용

                    Vector3 last = cursorPositionStack.get(cursorPositionStack.size() - 1);
                    Vector3 dir = new Vector3(last.x - add.x, last.y - add.y, 0);
                    float length = (float)Math.sqrt(dir.x * dir.x + dir.y * dir.y);
                    dir.x = dir.x / length * pathWidth;
                    dir.y = dir.y / length * pathWidth;

                    x = dir.x + add.x;
                    y = dir.y + add.y;
                    cursorPosition = new Vector3(x, y, 0);

                    //Broken Line 확인
                    boolean isH = add.y == last.y;

                    Rule rule = null;
                    if(isH) rule = hLineRules[(int)Math.min(last.x, add.x)][(int)add.y];
                    else rule = vLineRules[(int)add.x][(int)Math.min(last.y, add.y)];

                    if(rule instanceof BrokenLine) {
                        BrokenLine brokenLine = (BrokenLine) rule;

                        float dist = Math.abs(add.x - last.x) + Math.abs(add.y - last.y);
                        float maxDist = (1 - brokenLine.getSize()) / 2 - pathWidth / 2;
                        if (dist > maxDist) { //충돌했다
                            dir = new Vector3(add.x - last.x, add.y - last.y, 0);
                            length = (float) Math.sqrt(dir.x * dir.x + dir.y * dir.y);
                            dir.x = dir.x / length * maxDist;
                            dir.y = dir.y / length * maxDist;

                            cursorPosition.x = dir.x + last.x;
                            cursorPosition.y = dir.y + last.y;
                        }
                        return;
                    }
                }
                else{
                    //Broken Line 확인
                    Vector3 last = cursorPositionStack.get(cursorPositionStack.size() - 1);
                    boolean isH = add.y == last.y;

                    Rule rule = null;
                    if(isH) rule = hLineRules[(int)Math.min(last.x, add.x)][(int)add.y];
                    else rule = vLineRules[(int)add.x][(int)Math.min(last.y, add.y)];

                    if(rule instanceof BrokenLine){
                        BrokenLine brokenLine = (BrokenLine)rule;

                        float dist = Math.abs(add.x - last.x) + Math.abs(add.y - last.y);
                        float maxDist = (1 - brokenLine.getSize()) / 2 - pathWidth / 2;
                        if(dist > maxDist){ //충돌했다
                            Vector3 dir = new Vector3(add.x - last.x, add.y - last.y, 0);
                            float length = (float)Math.sqrt(dir.x * dir.x + dir.y * dir.y);
                            dir.x = dir.x / length * maxDist;
                            dir.y = dir.y / length * maxDist;

                            cursorPosition.x = dir.x + last.x;
                            cursorPosition.y = dir.y + last.y;
                        }
                        return;
                    }

                    cursorPositionStack.add(add);
                    cursorPosition = new Vector3(x, y, 0);
                }
                return;
            }
        }
    }

    @Override
    public int getVertexCount(){
        int vertexCount = 0;
        for(Shape shape : staticShapes){
            vertexCount += shape.getVertexCount();
        }
        for(Shape shape : dynamicShapes){
            vertexCount += shape.getVertexCount();
        }
        return vertexCount;
    }

    @Override
    public FloatBuffer getVertexBuffer(){
        ByteBuffer bb = ByteBuffer.allocateDirect(getVertexCount() * 3 * 4);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer vertexBuffer = bb.asFloatBuffer();

        for(Shape shape : staticShapes){
            shape.draw(vertexBuffer);
        }
        for(Shape shape : dynamicShapes){
            shape.draw(vertexBuffer);
        }

        vertexBuffer.position(0);

        return vertexBuffer;
    }

    @Override
    public FloatBuffer getVertexColorBuffer(){
        ByteBuffer bb = ByteBuffer.allocateDirect(getVertexCount() * 3 * 4);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer vertexBuffer = bb.asFloatBuffer();

        //FIXME: ConcurrentModificationException
        for(Shape shape : staticShapes){
            shape.drawColor(vertexBuffer);
        }
        for(Shape shape : dynamicShapes){
            shape.drawColor(vertexBuffer);
        }

        vertexBuffer.position(0);

        return vertexBuffer;
    }

    @Override
    public void setBackgroundColor(int color){
        backgroundColor = color;
    }

    @Override
    public int getBackgroundColor(){
        return backgroundColor;
    }

    @Override
    public void setPathColor(int color){
        pathColor = color;
    }

    @Override
    public int getPathColor(){
        return pathColor;
    }

    @Override
    public void setCursorColor(int color){
        cursorColor = color;
    }

    @Override
    public int getCursorColor(){
        return cursorColor;
    }

    @Override
    public float getSceneWidth() {
        return Math.max(width, height);
    }

    @Override
    public float getPathWidth() {
        return pathWidth;
    }

    @Override
    public boolean validate(ArrayList<Vector3> pathArrVec3) {
        ArrayList<Vector2Int> pathArr = new ArrayList<>();
        for(Vector3 vector3 : pathArrVec3){
            pathArr.add(new Vector2Int((int)vector3.x, (int)vector3.y));
        }

        Path path = new Path(this, pathArr);

        //Global Validation
        if(appliedRules.contains(Square.class)){
            if(!Square.validateGlobally(path)){
                Log.i("VAL", "SQUARE VALID FAILED");
                return false;
            }
        }

        //Local Validation
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height + 1; j++){
                if(hLineRules[i][j] != null && !hLineRules[i][j].validate(path)){
                    Log.i("VAL", "HLINE " + hLineRules[i][j] + " VALID FAILED");
                    return false;
                }
            }
        }
        for(int i = 0; i < width + 1; i++){
            for(int j = 0; j < height; j++){
                if(vLineRules[i][j] != null && !vLineRules[i][j].validate(path)){
                    Log.i("VAL", "VLINE " + vLineRules[i][j] + " VALID FAILED");
                    return false;
                }
            }
        }
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                if(tileRules[i][j] != null && !tileRules[i][j].validate(path)){
                    Log.i("VAL", "TILE " + tileRules[i][j] + " VALID FAILED");
                    return false;
                }
            }
        }
        for(int i = 0; i <= width; i++){
            for(int j = 0; j <= height; j++){
                if(cornerRules[i][j] != null && !cornerRules[i][j].validate(path)){
                    Log.i("VAL", "CORNER " + cornerRules[i][j] + " VALID FAILED");
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Rule[][] getTileRules() {
        return tileRules;
    }

    @Override
    public Rule[][] getHLineRules() {
        return hLineRules;
    }

    @Override
    public Rule[][] getVLineRules() {
        return vLineRules;
    }

    @Override
    public Rule[][] getCornerRules() {
        return cornerRules;
    }

    public void addRule(Rule rule){
        if(rule.site == Rule.Site.TILE){
            tileRules[rule.x][rule.y] = rule;
        }
        else if(rule.site == Rule.Site.HLINE){
            hLineRules[rule.x][rule.y] = rule;
        }
        else if(rule.site == Rule.Site.VLINE){
            vLineRules[rule.x][rule.y] = rule;
        }
        else if(rule.site == Rule.Site.CORNER){
            cornerRules[rule.x][rule.y] = rule;
        }
        appliedRules.add(rule.getClass());
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void addStartingPoint(StartingPoint startingPoint){
        startingPoints.add(startingPoint);
    }

    public void addEndingPoint(EndingPoint endingPoint){
        endingPoints.add(endingPoint);
    }


}
