package ooga.view;

public class PerkTreeNode {

  private final PerkView perkView;
  private PerkTreeNode left;
  private PerkTreeNode right;

  public PerkTreeNode(PerkView perkView) {
    this.perkView = perkView;
    this.left = null;
    this.right = null;
  }

  public PerkTreeNode(PerkView perkView, PerkTreeNode left, PerkTreeNode right) {
    this.perkView = perkView;
    this.left = left;
    this.right = right;
  }

  public void setLeft(PerkTreeNode perkTeeNode) {
    this.left = perkTeeNode;
  }

  public void setRight(PerkTreeNode perkTeeNode) {
    this.right = perkTeeNode;
  }

  public PerkTreeNode left() {
    return left;
  }

  public PerkTreeNode right() {
    return right;
  }

  public PerkView value() {
    return perkView;
  }
}
