package cn.sucang.widget.spinkit;

import cn.sucang.widget.spinkit.sprite.Sprite;
import cn.sucang.widget.spinkit.style.ChasingDots;
import cn.sucang.widget.spinkit.style.Circle;
import cn.sucang.widget.spinkit.style.CubeGrid;
import cn.sucang.widget.spinkit.style.DoubleBounce;
import cn.sucang.widget.spinkit.style.FadingCircle;
import cn.sucang.widget.spinkit.style.FoldingCube;
import cn.sucang.widget.spinkit.style.MultiplePulse;
import cn.sucang.widget.spinkit.style.MultiplePulseRing;
import cn.sucang.widget.spinkit.style.Pulse;
import cn.sucang.widget.spinkit.style.PulseRing;
import cn.sucang.widget.spinkit.style.RotatingCircle;
import cn.sucang.widget.spinkit.style.RotatingPlane;
import cn.sucang.widget.spinkit.style.ThreeBounce;
import cn.sucang.widget.spinkit.style.WanderingCubes;
import cn.sucang.widget.spinkit.style.Wave;

/**
 * Created by ybq.
 */
public class SpriteFactory
{

    public static Sprite create(Style style)
    {
        Sprite sprite = null;
        switch (style)
        {
            case ROTATING_PLANE:
                sprite = new RotatingPlane();
                break;
            case DOUBLE_BOUNCE:
                sprite = new DoubleBounce();
                break;
            case WAVE:
                sprite = new Wave();
                break;
            case WANDERING_CUBES:
                sprite = new WanderingCubes();
                break;
            case PULSE:
                sprite = new Pulse();
                break;
            case CHASING_DOTS:
                sprite = new ChasingDots();
                break;
            case THREE_BOUNCE:
                sprite = new ThreeBounce();
                break;
            case CIRCLE:
                sprite = new Circle();
                break;
            case CUBE_GRID:
                sprite = new CubeGrid();
                break;
            case FADING_CIRCLE:
                sprite = new FadingCircle();
                break;
            case FOLDING_CUBE:
                sprite = new FoldingCube();
                break;
            case ROTATING_CIRCLE:
                sprite = new RotatingCircle();
                break;
            case MULTIPLE_PULSE:
                sprite = new MultiplePulse();
                break;
            case PULSE_RING:
                sprite = new PulseRing();
                break;
            case MULTIPLE_PULSE_RING:
                sprite = new MultiplePulseRing();
                break;
            default:
                break;
        }
        return sprite;
    }
}
