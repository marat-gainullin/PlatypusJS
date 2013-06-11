/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.script.ScriptUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public class ServerReportRunnerPrototype extends IdScriptableObject {

    public static final String BAD_SCRIPT_SCOPE_MSG = "Can't find reqired script runner scope!";
    private static final String ID_CONSTRUCTOR = "constructor";
    private static final String ID_TOSOURCE = "toSource";
    private static final String ID_TOSTRING = "toString";
    private static final String ID_TOLOCALESTRING = "toLocaleString";
    private static final String REPORT_TAG = "Report";
    private static final String ONLY_CONSTRUCTOR_MSG = "Can't call %s(...). Only new %s(...) is allowed.";
    private static final String CONSTRUCTOR_PARAMETER_MISSING = "For new %s(...) constructor, module name/id parameter is required.";

    public static void init(Scriptable scope, boolean sealed) {
        ServerReportRunnerPrototype obj = new ServerReportRunnerPrototype();
        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, sealed);
    }

    @Override
    public String getClassName() {
        return REPORT_TAG;
    }

    public ServerReportRunnerPrototype() {
        super();
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        switch (id) {
            case Id_constructor:
                arity = 1;
                s = ID_CONSTRUCTOR;
                break;
            case Id_toString:
                arity = 1;
                s = ID_TOSTRING;
                break;
            case Id_toLocaleString:
                arity = 1;
                s = ID_TOLOCALESTRING;
                break;
            case Id_toSource:
                arity = 0;
                s = ID_TOSOURCE;
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(REPORT_TAG, id, s, arity);
    }

    @Override
    protected int findPrototypeId(String s) {
        switch (s) {
            case ID_CONSTRUCTOR:
                return Id_constructor;
            case ID_TOSTRING:
                return Id_toString;
            case ID_TOLOCALESTRING:
                return Id_toLocaleString;
            case ID_TOSOURCE:
                return Id_toSource;
            default:
                return 0;
        }
    }
    private static final int Id_constructor = 1,
            Id_toString = 2,
            Id_toLocaleString = 3,
            Id_toSource = 4,
            MAX_PROTOTYPE_ID = 4;

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope,
            Scriptable thisObj, Object[] args) {
        if (!f.hasTag(REPORT_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        if (id == Id_constructor) {
            if (args.length >= 1) {
                if (thisObj == null) {
                    if (args[0] != null) {
                        if (thisObj == null) {
                            String scriptId = args[0].toString();
                            try {
                                ServerScriptRunner serverCoreWrapper = lookupScriptRunner(scope);
                                assert serverCoreWrapper != null : BAD_SCRIPT_SCOPE_MSG;
                                ModuleConfig config = new ModuleConfig(false, true, false, (String) null, scriptId);
                                return new ServerReportRunner(serverCoreWrapper.getServerCore(),
                                        serverCoreWrapper.getCreationSession(),
                                        config,
                                        ScriptUtils.getScope(),
                                        serverCoreWrapper.getServerCore(),
                                        serverCoreWrapper.getServerCore(),
                                        serverCoreWrapper.getScriptResolverHost());
                            } catch (Exception ex) {
                                throw new IllegalArgumentException(ex);
                            }
                        } else {
                            throw new IllegalArgumentException(String.format(ONLY_CONSTRUCTOR_MSG, REPORT_TAG, REPORT_TAG));
                        }
                    } else {
                        throw new IllegalArgumentException(String.format(CONSTRUCTOR_PARAMETER_MISSING, REPORT_TAG, REPORT_TAG));
                    }
                } else {
                    throw incompatibleCallError(f);
                }
            } else {
                throw incompatibleCallError(f);
            }
        }

        // The rest of Module.prototype methods require thisObj to be ScriptRunner

        if (!(thisObj instanceof ServerReportRunner)) {
            throw incompatibleCallError(f);
        }
        ServerScriptRunner thisReportRunner = (ServerReportRunner) thisObj;
        switch (id) {

            case Id_toString:
            case Id_toLocaleString: {
                // toLocaleString is just an alias for toString for now
                return String.format("Platypus report ( %s )", thisReportRunner.getModuleId());
            }

            case Id_toSource:
                return "function Report(){\n/*compiled code*/\n}";

            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    public static ServerReportRunner lookupScriptRunner(Scriptable aScope) {
        Scriptable currentScope = aScope;
        while (currentScope != null && !(currentScope instanceof ServerScriptRunner)) {
            currentScope = currentScope.getParentScope();
        }
        return (ServerReportRunner) currentScope;
    }
}