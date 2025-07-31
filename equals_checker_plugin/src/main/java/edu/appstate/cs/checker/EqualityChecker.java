package edu.appstate.cs.checker;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.*;
import com.sun.tools.javac.code.Type;

import javax.lang.model.element.Name;

import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;

@AutoService(BugChecker.class)
@BugPattern(
        name = "BadNamesChecker",
        summary = "Poor-quality identifiers",
        severity = WARNING,
        linkType = CUSTOM,
        link = "https://github.com/plse-Lab/"
)
public class BadNamesChecker extends BugChecker implements
        BugChecker.IdentifierTreeMatcher,
        BugChecker.MethodInvocationTreeMatcher,
        BugChecker.MethodTreeMatcher,
        BugChecker.IfTreeMatcher,
        BugChecker.BinaryTreeMatcher //probably needs to be removed.

        {

    @java.lang.Override
    public Description matchIdentifier(IdentifierTree identifierTree, VisitorState visitorState) {
        // NOTE: This matches identifier uses. Do we want to match these,
        // or just declarations?
        Name identifier = identifierTree.getName();
        return checkName(identifierTree, identifier);
    }

    @Override
    public Description matchIf(IfTree tree, VisitorState state) {
        if (tree.getElseStatement() == null) {
            return buildDescription(tree)
                .setMessage("We found an if without an else")
                .build();
        }
        return Description.NO_MATCH;
    }

    @Override
    public Description matchMethodInvocation(MethodInvocationTree methodInvocationTree, VisitorState visitorState) {
        // NOTE: Similarly to the above, this matches method names in method
        // calls. Do we want to match these, or just declarations?
        Tree methodSelect = methodInvocationTree.getMethodSelect();

        Name identifier;

        if (methodSelect instanceof MemberSelectTree) {
            identifier = ((MemberSelectTree) methodSelect).getIdentifier();
        } else if (methodSelect instanceof IdentifierTree) {
            identifier = ((IdentifierTree) methodSelect).getName();
        } else {
            throw malformedMethodInvocationTree(methodInvocationTree);
        }

        return checkName(methodInvocationTree, identifier);
    }

    @Override
    public Description matchMethod(MethodTree methodTree, VisitorState visitorState) {
        // MethodTree represents the definition of a method. We want to check the name of this
        // method to see if it is acceptable.

        // TODO: What needs to be done here to check the name of the method?
        Name identifier = methodTree.getName();
        return checkName(methodTree, identifier);

        // TODO: Remove this, if needed. This is just here because we need to return a Description.
        // return Description.NO_MATCH;
    }

    private Description checkName(Tree tree, Name identifier) {
        // TODO: What other names are a problem? Add checks for them here...
        if (identifier.contentEquals("foo") || identifier.contentEquals("bar")) {
            return buildDescription(tree)
                    .setMessage(String.format("%s is a bad identifier name", identifier))
                    .build();
        }

        return Description.NO_MATCH;
    }


    @Override
    public Description matchBinary(BinaryTree binaryTree, VisitorState visitorState){
        if(binaryTree.getKind() == Kind.EQUAL_TO){
            //BinaryTree binaryTree = (BinaryTree) tree;

            ExpressionTree leftOperand = binaryTree.getLeftOperand();
            ExpressionTree rightOperand = binaryTree.getRightOperand();

            Type leftside = ASTHelpers.getType(leftOperand);
            Type rightside = ASTHelpers.getType(rightOperand);



            if(leftside.isPrimitive() && rightside.isPrimitive())
            {
                return Description.NO_MATCH;
            }

            return buildDescription(binaryTree)
                    .setMessage(String.format("Use .equals() instead of == for comparison of primitive types."))
                    .build();
           

        }

        return Description.NO_MATCH; 
    }




    private static final IllegalStateException malformedMethodInvocationTree(MethodInvocationTree tree) {
        return new IllegalStateException(String.format("Method name %s is malformed.", tree));
    }
}