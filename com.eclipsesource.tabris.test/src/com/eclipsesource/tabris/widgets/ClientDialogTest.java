/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.tabris.widgets;

import static com.eclipsesource.tabris.internal.Constants.EVENT_SELECTION;
import static com.eclipsesource.tabris.test.TabrisTestUtil.mockRemoteObject;
import static com.eclipsesource.tabris.widgets.ClientDialog.Severity.INFO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.Serializable;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import com.eclipsesource.tabris.internal.Constants;
import com.eclipsesource.tabris.test.TabrisTestUtil;
import com.eclipsesource.tabris.widgets.ClientDialog.ButtonType;
import com.eclipsesource.tabris.widgets.ClientDialog.Severity;


public class ClientDialogTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( ClientDialog.class ) );
  }

  @Test
  public void testFailsWithNullTitle() {
    thrown.expect( IllegalArgumentException.class );
    thrown.expectMessage( "title must not be null" );

    ClientDialog dialog = new ClientDialog();
    dialog.setTitle( null );
  }

  @Test
  public void testSetsTitle() {
    ClientDialog dialog = new ClientDialog();

    dialog.setTitle( "foo" );

    String title = dialog.getTitle();
    assertEquals( "foo", title );
  }

  @Test
  public void testSetsTitleOnRemoteObject() {
    RemoteObject remoteObject = mockRemoteObject();
    ClientDialog dialog = new ClientDialog();

    dialog.setTitle( "foo" );

    verify( remoteObject ).set( "title", "foo" );
  }

  @Test
  public void testFailsToSetNullMessage() {
    thrown.expect( IllegalArgumentException.class );
    thrown.expectMessage( "message must not be null" );

    ClientDialog dialog = new ClientDialog();
    dialog.setMessage( null );
  }

  @Test
  public void testSetsMessage() {
    ClientDialog dialog = new ClientDialog();

    dialog.setMessage( "bar" );

    String message = dialog.getMessage();
    assertEquals( "bar", message );
  }

  @Test
  public void testSetsMessageOnRemoteObject() {
    RemoteObject remoteObject = mockRemoteObject();
    ClientDialog dialog = new ClientDialog();

    dialog.setMessage( "bar" );

    verify( remoteObject ).set( "message", "bar" );
  }

  @Test
  public void testSetMessageReturnsDialog() {
    ClientDialog dialog = new ClientDialog();

    ClientDialog actualDialog = dialog.setMessage( "bar" );

    assertSame( dialog, actualDialog );
  }

  @Test
  public void testFailsToSetNullSeverity() {
    thrown.expect( IllegalArgumentException.class );
    thrown.expectMessage( "severity must not be null" );

    ClientDialog dialog = new ClientDialog();
    dialog.setSeverity( null );
  }

  @Test
  public void testSetsSeverity() {
    ClientDialog dialog = new ClientDialog();

    dialog.setSeverity( INFO );

    Severity severity = dialog.getSeverity();
    assertSame( INFO, severity );
  }

  @Test
  public void testSetsSeverityOnRemoteObject() {
    RemoteObject remoteObject = mockRemoteObject();
    ClientDialog dialog = new ClientDialog();

    dialog.setSeverity( INFO );

    verify( remoteObject ).set( "severity", INFO.toString() );
  }

  @Test
  public void testSetSeverityReturnsDialog() {
    ClientDialog dialog = new ClientDialog();

    ClientDialog actualDialog = dialog.setSeverity( INFO );

    assertSame( dialog, actualDialog );
  }

  @Test
  public void testOpenCreatesCallOperation() {
    RemoteObject remoteObject = mockRemoteObject();
    ClientDialog dialog = new ClientDialog();

    dialog.open();

    verify( remoteObject ).call( "open", null );
  }

  @Test
  public void testCloseCreatesCallOperation() {
    RemoteObject remoteObject = mockRemoteObject();
    ClientDialog dialog = new ClientDialog();

    dialog.close();

    verify( remoteObject ).call( "close", null );
  }

  @Test
  public void testNotifiesListenerOnSelectionEvent() {
    ClientDialog dialog = new ClientDialog();
    Listener listener = mock( Listener.class );
    JsonObject properties = new JsonObject();
    properties.add( "buttonType", "buttonOk" );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    dialog.setButton( ButtonType.OK, "bar", listener );
    TabrisTestUtil.dispatchNotify( dialog.getRemoteObject(), "Selection", properties );

    verify( listener ).handleEvent( any( Event.class ) );
  }

  @Test
  public void testNotifiesListenerOnSelectionEventWithCorrectDisplay() {
    Display display = new Display();
    ClientDialog dialog = new ClientDialog();
    Listener listener = mock( Listener.class );
    JsonObject properties = new JsonObject();
    properties.add( "buttonType", "buttonOk" );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    dialog.setButton( ButtonType.OK, "bar", listener );
    TabrisTestUtil.dispatchNotify( dialog.getRemoteObject(), "Selection", properties );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    assertSame( display, captor.getValue().display );
  }

  @Test
  public void testSetsButtonSetsButtonPropertyOnRemoteObject() {
    RemoteObject remoteObject = mockRemoteObject();
    ClientDialog dialog = new ClientDialog();
    Listener listener = mock( Listener.class );

    dialog.setButton( ButtonType.OK, "bar", listener );

    verify( remoteObject ).set( "buttonOk", "bar" );
  }

  @Test
  public void testSetsButtonCreateListenOnRemoteObject() {
    RemoteObject remoteObject = mockRemoteObject();
    ClientDialog dialog = new ClientDialog();
    Listener listener = mock( Listener.class );

    dialog.setButton( ButtonType.OK, "bar", listener );

    verify( remoteObject ).listen( EVENT_SELECTION, true );
  }

  @Test
  public void testSetsButtonCreateListenOnRemoteObjectOnce() {
    RemoteObject remoteObject = mockRemoteObject();
    ClientDialog dialog = new ClientDialog();
    Listener listener = mock( Listener.class );

    dialog.setButton( ButtonType.OK, "bar", listener );
    dialog.setButton( ButtonType.OK, "bar", listener );

    verify( remoteObject, times( 1 ) ).listen( Constants.EVENT_SELECTION, true );
  }

  @Test
  public void testSetsButtonWithoutListenerSetsButtonPropertyOnRemoteObject() {
    RemoteObject remoteObject = mockRemoteObject();
    ClientDialog dialog = new ClientDialog();

    dialog.setButton( ButtonType.OK, "bar" );

    verify( remoteObject ).set( "buttonOk", "bar" );
  }

  @Test
  public void testFailsToSetButtonWithNullType() {
    thrown.expect( IllegalArgumentException.class );
    thrown.expectMessage( "type must not be null" );

    ClientDialog dialog = new ClientDialog();
    dialog.setButton( null, "bar", mock( Listener.class ) );
  }

  @Test
  public void testFailsToSetButtonWithNullText() {
    thrown.expect( IllegalArgumentException.class );
    thrown.expectMessage( "text must not be null" );

    ClientDialog dialog = new ClientDialog();
    dialog.setButton( ButtonType.OK, null, mock( Listener.class ) );
  }

  @Test
  public void testFailsToSetButtonWithEmptyText() {
    thrown.expect( IllegalArgumentException.class );
    thrown.expectMessage( "text must not be empty" );

    ClientDialog dialog = new ClientDialog();
    dialog.setButton( ButtonType.OK, "", mock( Listener.class ) );
  }

  @Test
  public void testFailsToSetButtonWithNullTypeWithoutListener() {
    thrown.expect( IllegalArgumentException.class );
    thrown.expectMessage( "type must not be null" );

    ClientDialog dialog = new ClientDialog();
    dialog.setButton( null, "bar" );
  }

  @Test
  public void testFailsToSetButtonWithNullTextWithoutListener() {
    thrown.expect( IllegalArgumentException.class );
    thrown.expectMessage( "text must not be null" );

    ClientDialog dialog = new ClientDialog();
    dialog.setButton( ButtonType.OK, null );
  }

  @Test
  public void testFailsToSetButtonWithEmptyTextWithoutListener() {
    thrown.expect( IllegalArgumentException.class );
    thrown.expectMessage( "text must not be empty" );

    ClientDialog dialog = new ClientDialog();
    dialog.setButton( ButtonType.OK, "" );
  }

  @Test
  public void testFailsToAddNullClientListener() {
    thrown.expect( IllegalArgumentException.class );
    thrown.expectMessage( "listener must not be null" );

    ClientDialog dialog = new ClientDialog();
    dialog.addClientDialogListener( null );
  }

  @Test
  public void testFailsToRemoveNullClientListener() {
    thrown.expect( IllegalArgumentException.class );
    thrown.expectMessage( "listener must not be null" );

    ClientDialog dialog = new ClientDialog();
    dialog.removeClientDialogListener( null );
  }

  @Test
  public void testAddClientDialogListenerSendsListen() {
    RemoteObject remoteObject = mockRemoteObject();
    ClientDialog dialog = new ClientDialog();

    dialog.addClientDialogListener( mock( ClientDialogListener.class ) );

    verify( remoteObject ).listen( "ClientDialogClose", true );
  }

  @Test
  public void testAddClientDialogListenersSendsListenOnce() {
    RemoteObject remoteObject = mockRemoteObject();
    ClientDialog dialog = new ClientDialog();

    dialog.addClientDialogListener( mock( ClientDialogListener.class ) );
    dialog.addClientDialogListener( mock( ClientDialogListener.class ) );

    verify( remoteObject, times( 1 ) ).listen( "ClientDialogClose", true );
  }

  @Test
  public void testRemoveClientDialogListenerSendsListen() {
    RemoteObject remoteObject = mockRemoteObject();
    ClientDialog dialog = new ClientDialog();
    ClientDialogListener listener = mock( ClientDialogListener.class );
    dialog.addClientDialogListener( listener );

    dialog.removeClientDialogListener( listener );

    verify( remoteObject ).listen( "ClientDialogClose", false );
  }

  @Test
  public void testOpenCallsClientDialogListener() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    ClientDialog dialog = new ClientDialog();
    ClientDialogListener listener = mock( ClientDialogListener.class );
    dialog.addClientDialogListener( listener );

    dialog.open();

    verify( listener ).open();
  }

  @Test
  public void testCloseEventCallsClientDialogListener() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    ClientDialog dialog = new ClientDialog();
    ClientDialogListener listener = mock( ClientDialogListener.class );
    dialog.addClientDialogListener( listener );

    TabrisTestUtil.dispatchNotify( dialog.getRemoteObject(), "ClientDialogClose", null );

    verify( listener ).close();
  }
}
