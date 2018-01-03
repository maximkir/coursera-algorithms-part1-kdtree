import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class KdTree {

    private Node root;

    private int size = 0;

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return this.size;
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (root == null) {
            root = Node.create(p, 0,null, null);
            size++;
        } else if (!contains(p)) {
            insert(root, p, true);
            size++;
        }
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (isEmpty()) return false;
        return contains(root, p, true);
    }

    public void draw() {
        draw(root, new RectHV(0,0, 1, 1));
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        List<Point2D> result = new LinkedList<>();
        range(this.root, rect, result);
        return result;
    }

    public Point2D nearest(Point2D p) {
        if (p == null || isEmpty()) throw new IllegalArgumentException();
        return nearest(p, root.data, root);
    }

    private Point2D nearest(Point2D p, Point2D currentNearestPoint, Node n){

        if (n == null){
            return currentNearestPoint;
        }
        if (n.nodeLevel % 2 == 0){
            if (p.x() > n.data.x()){ // check right sub-tree
                Point2D npr = nearest(p, n.data.distanceTo(p) < currentNearestPoint.distanceTo(p) ? n.data : currentNearestPoint, n.right);
                if (npr.distanceTo(p) > Math.abs(n.data.x() - p.x())){
                    Point2D npl = nearest(p, npr, n.left);
                    return npr.distanceTo(p) > npl.distanceTo(p) ? npl : npr;
                } else {
                    return npr;
                }
            } else { // check left sub-tree
                Point2D npl = nearest(p, n.data.distanceTo(p) < currentNearestPoint.distanceTo(p) ? n.data : currentNearestPoint, n.left);
                if (npl.distanceTo(p) > Math.abs(n.data.x() - p.x())) {
                    Point2D npr = nearest(p, npl, n.right);
                    return npr.distanceTo(p) > npl.distanceTo(p) ? npl : npr;
                } else {
                    return npl;
                }
            }
        } else {
            if (p.y() > n.data.y()) { // check up sub-tree
                Point2D npu = nearest(p, n.data.distanceTo(p) < currentNearestPoint.distanceTo(p) ? n.data : currentNearestPoint, n.right);
                if (npu.distanceTo(p) > Math.abs(n.data.y() - p.y())){
                    Point2D npd = nearest(p, npu, n.left);
                    return npu.distanceTo(p) > npd.distanceTo(p) ? npd : npu;
                } else {
                    return npu;
                }
            } else { // check down sub-tree
                Point2D npd = nearest(p, n.data.distanceTo(p) < currentNearestPoint.distanceTo(p) ? n.data : currentNearestPoint, n.left);
                if (npd.distanceTo(p) > Math.abs(n.data.y() - p.y())){
                    Point2D npu = nearest(p, npd, n.right);
                    return npu.distanceTo(p) > npd.distanceTo(p) ? npd : npu;
                } else {
                    return npd;
                }
            }
        }
    }

    private void range(Node n, RectHV rect, List<Point2D> acc){
        if (n == null){
            return;
        }

        if (rect.contains(n.data)){
            acc.add(n.data);
        }

        if (n.nodeLevel % 2 == 0) { //Vertical segment
            // the vertical line intersects with query rectangle
            if (rect.xmin() <= n.data.x() && n.data.x() <= rect.xmax()) {
                range(n.left, rect, acc);
                range(n.right, rect, acc);
            } else if (rect.xmin() > n.data.x()){ // Search right
                range(n.right, rect, acc);
            } else { // Search left
                range(n.left, rect, acc);
            }
        } else { // Horizontal segment
            // the horizontal line intersects with query rectangle
            if (rect.ymin() <= n.data.y() && n.data.y() <= rect.ymax()) {
                range(n.left, rect, acc);
                range(n.right, rect, acc);
            } else if (rect.ymin() > n.data.y()){ // Search up
                range(n.right, rect, acc);
            } else { // Search down
                range(n.left, rect, acc);
            }
        }
    }

    private void draw(Node n, RectHV rectHV){
        if (n == null){
            return;
        }
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.02);
        n.data.draw();

        StdDraw.setPenRadius(0.001);
        if (n.nodeLevel % 2 == 0){
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(n.data.x(), rectHV.ymin(), n.data.x(), rectHV.ymax());
            draw(n.left, new RectHV(rectHV.xmin(), rectHV.ymin(), n.data.x(), rectHV.ymax()));
            draw(n.right, new RectHV(n.data.x(), rectHV.ymin(), rectHV.xmax(), rectHV.ymax()));
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(rectHV.xmin(), n.data.y(), rectHV.xmax(), n.data.y());
            draw(n.left, new RectHV(rectHV.xmin(), rectHV.ymin(), rectHV.xmax(), n.data.y()));
            draw(n.right, new RectHV(rectHV.xmin(), n.data.y(), rectHV.xmax(), rectHV.ymax()));
        }
    }

    private boolean contains(Node root, Point2D p, boolean useX){
        Comparator<Point2D> comp = useX ? Point2D.X_ORDER : Point2D.Y_ORDER;
        if (root.data.equals(p)) return true;
        if (comp.compare(root.data, p) > 0) {
            if (root.left == null){
                return false;
            } else {
                return contains(root.left, p, !useX);
            }
        } else if (root.right == null){
            return false;
        } else {
            return contains(root.right, p, !useX);
        }
    }


    private void insert(Node root, Point2D p, boolean useX){
        Comparator<Point2D> comp = useX ? Point2D.X_ORDER : Point2D.Y_ORDER;
        // if the point to be inserted is smaller, go left
        if (comp.compare(root.data, p) > 0) {
            if (root.left == null){
                root.left = Node.create(p, root.nodeLevel + 1, null, null);
            } else {
                insert(root.left, p, !useX);
            }
        } else if (root.right == null){
            root.right = Node.create(p, root.nodeLevel + 1,null, null);
        } else {
            insert(root.right, p, !useX);
        }
    }

    /**
     * Represents a Node in a Kd tree
     *
     *
     */
    private static class Node {

        private final Point2D data;
        private Node left;
        private Node right;
        private final int nodeLevel;

        private Node(Point2D data, int nodeLevel, Node left, Node right) {
            this.data = data;
            this.left = left;
            this.right = right;
            this.nodeLevel = nodeLevel;
        }

        public static Node create(Point2D data, int nodeLevel, Node left, Node right){
            return new Node(data, nodeLevel, left, right);
        }
    }

    public static void main(String[] args) {
        KdTree kdTree = new KdTree();

        kdTree.insert(new Point2D(0.7, 0.2)); // A
        kdTree.insert(new Point2D(0.5, 0.4)); // B
        kdTree.insert(new Point2D(0.2, 0.3)); // C
        kdTree.insert(new Point2D(0.4, 0.7)); // D
        kdTree.insert(new Point2D(0.9, 0.6)); // E


        System.out.println(kdTree.nearest(new Point2D(0.078, 0.552)));
        System.out.println(kdTree.nearest(new Point2D(0.684, 0.73)));

    }
}
