/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.tabris.ui;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.Serializable;
import java.util.List;

import org.eclipse.swt.graphics.RGB;
import org.junit.Rule;
import org.junit.Test;

import com.eclipsesource.tabris.internal.ui.ImageUtil;
import com.eclipsesource.tabris.internal.ui.PageDescriptor;
import com.eclipsesource.tabris.internal.ui.TestAction;
import com.eclipsesource.tabris.internal.ui.TestPage;
import com.eclipsesource.tabris.internal.ui.UIDescriptor;
import com.eclipsesource.tabris.internal.ui.UITestUtil;
import com.eclipsesource.tabris.internal.ui.UIUpdater;
import com.eclipsesource.tabris.internal.ui.UpdateUtil;
import com.eclipsesource.tabris.test.util.TabrisEnvironment;


public class UIConfigurationTest {

  @Rule
  public TabrisEnvironment environment = new TabrisEnvironment();

  @Test
  public void testIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( UIConfiguration.class ) );
  }

  @Test
  public void testTransistionListenerIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( TransitionListener.class ) );
  }

  @Test( expected = IllegalStateException.class )
  public void testAddPageConfigurationFailsWithDuplicateId() {
    UIConfiguration configuration = new UIConfiguration();
    PageConfiguration configuration1 = new PageConfiguration( "foo", TestPage.class );
    PageConfiguration configuration2 = new PageConfiguration( "foo", TestPage.class );

    configuration.addPageConfiguration( configuration1 );
    configuration.addPageConfiguration( configuration2 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddPageConfigurationFailsWithNullConfiguration() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.addPageConfiguration( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddActionConfigurationFailsWithNullConfiguration() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.addActionConfiguration( null );
  }

  @Test( expected = IllegalStateException.class )
  public void testAddActionConfigurationFailsWithDuplicateId() {
    UIConfiguration configuration = new UIConfiguration();
    ActionConfiguration configuration1 = new ActionConfiguration( "foo", TestAction.class );
    ActionConfiguration configuration2 = new ActionConfiguration( "foo", TestAction.class );

    configuration.addActionConfiguration( configuration1 );
    configuration.addActionConfiguration( configuration2 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddTransitionListenerFailsWithNullListener() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.addTransitionListener( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveTransitionListenerFailsWithNullListener() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.removeTransitionListener( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testAddActionListenerFailsWithNullListener() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.addActionListener( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveActionListenerFailsWithNullListener() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.removeActionListener( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetPageConfigurationFailsWithNullId() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.getPageConfiguration( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetPageConfigurationFailsWithEmptyId() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.getPageConfiguration( "" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemovePageConfigurationFailsWithNullId() {
    UIConfiguration configuration = new UIConfiguration();
    PageConfiguration pageConfiguration = new PageConfiguration( "foo", TestPage.class );
    configuration.addPageConfiguration( pageConfiguration );

    configuration.removePageConfiguration( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemovePageConfigurationFailsWithEmptyId() {
    UIConfiguration configuration = new UIConfiguration();
    PageConfiguration pageConfiguration = new PageConfiguration( "foo", TestPage.class );
    configuration.addPageConfiguration( pageConfiguration );

    configuration.removePageConfiguration( "" );
  }

  @Test
  public void testRemovePageConfigurationDeletesPageConfiguration() {
    UIConfiguration configuration = new UIConfiguration();
    PageConfiguration pageConfiguration = new PageConfiguration( "foo", TestPage.class );
    configuration.addPageConfiguration( pageConfiguration );

    configuration.removePageConfiguration( "foo" );

    PageConfiguration actualPageConfiguration = configuration.getPageConfiguration( "foo" );
    assertNull( actualPageConfiguration );
  }

  @Test
  public void testRemovePageConfigurationTriggersUpdate() {
    UIUpdater updater = mock( UIUpdater.class );
    UpdateUtil.registerUpdater( updater );
    UIConfiguration configuration = new UIConfiguration();
    PageConfiguration pageConfiguration = new PageConfiguration( "foo", TestPage.class );
    configuration.addPageConfiguration( pageConfiguration );

    configuration.removePageConfiguration( "foo" );

    verify( updater ).remove( pageConfiguration );
  }

  @Test
  public void testRemovePageConfigurationDeletesPageConfigurationFromDescriptor() {
    UIConfiguration configuration = new UIConfiguration();
    PageConfiguration pageConfiguration = new PageConfiguration( "foo", TestPage.class );
    configuration.addPageConfiguration( pageConfiguration );

    configuration.removePageConfiguration( "foo" );

    UIDescriptor uiDescriptor = configuration.getAdapter( UIDescriptor.class );
    PageDescriptor pageDescriptor = uiDescriptor.getPageDescriptor( "foo" );
    assertNull( pageDescriptor );
  }

  @Test
  public void testHasPageConfigurationForId() {
    UIConfiguration configuration = new UIConfiguration();
    PageConfiguration pageConfiguration = new PageConfiguration( "foo", TestPage.class );
    configuration.addPageConfiguration( pageConfiguration );

    PageConfiguration actualPageConfiguration = configuration.getPageConfiguration( "foo" );

    assertSame( pageConfiguration, actualPageConfiguration );
  }

  @Test
  public void testGetPageConfigurationReturnsNullForNonexistingPage() {
    UIConfiguration configuration = new UIConfiguration();
    PageConfiguration pageConfiguration = new PageConfiguration( "foo", TestPage.class );
    configuration.addPageConfiguration( pageConfiguration );

    PageConfiguration actualPageConfiguration = configuration.getPageConfiguration( "foo2" );

    assertNull( actualPageConfiguration );
  }

  @Test
  public void testAddActionConfigurationReturnsSameUiInstance() {
    UIConfiguration configuration = new UIConfiguration();

    UIConfiguration actualConf
      = configuration.addActionConfiguration( new ActionConfiguration( "foo", TestAction.class ) );

    assertSame( configuration, actualConf );
  }

  @Test
  public void testAddActionConfigurationWithProminenceReturnsSameUiInstance() {
    UIConfiguration configuration = new UIConfiguration();

    UIConfiguration actualConf
      = configuration.addActionConfiguration( new ActionConfiguration( "foo", TestAction.class ) );

    assertSame( configuration, actualConf );
  }

  @Test
  public void testAddPageConfigurationDoesNotReturnNull() {
    UIConfiguration configuration = new UIConfiguration();
    PageConfiguration pageConfiguration = new PageConfiguration( "foo", TestPage.class ).setTopLevel( true );

    UIConfiguration actualConf = configuration.addPageConfiguration( pageConfiguration );

    assertNotNull( actualConf );
  }

  @Test
  public void testGetDescriptorIsNotSafeCopy() {
    UIConfiguration configuration = new UIConfiguration();

    UIDescriptor contentHolder1 = configuration.getAdapter( UIDescriptor.class );
    UIDescriptor contentHolder2 = configuration.getAdapter( UIDescriptor.class );

    assertSame( contentHolder1, contentHolder2 );
    assertNotNull( contentHolder1 );
  }

  @Test
  public void testCanGetDescriptor() {
    UIConfiguration configuration = new UIConfiguration();

    UIDescriptor uiDescriptor = configuration.getAdapter( UIDescriptor.class );

    assertNotNull( uiDescriptor );
  }

  @Test
  public void testAddsPageToHolder() {
    UIConfiguration configuration = new UIConfiguration();
    PageConfiguration pageConfiguration = new PageConfiguration( "foo", TestPage.class ).setTopLevel( true );

    configuration.addPageConfiguration( pageConfiguration );

    PageDescriptor actualDescriptor = configuration.getAdapter( UIDescriptor.class ).getPageDescriptor( "foo" );
    assertNotNull( actualDescriptor );
  }

  @Test
  public void testAddsPageTriggersUIUpdate() {
    UIUpdater updater = mock( UIUpdater.class );
    UpdateUtil.registerUpdater( updater );
    UIConfiguration configuration = new UIConfiguration();
    PageConfiguration pageConfiguration = new PageConfiguration( "foo", TestPage.class ).setTopLevel( true );

    configuration.addPageConfiguration( pageConfiguration );

    verify( updater ).update( configuration );
  }

  @Test
  public void testAddsActionToHolder() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.addActionConfiguration( new ActionConfiguration( "foo", TestAction.class ) );

    assertNotNull( configuration.getAdapter( UIDescriptor.class ).getActionDescriptor( "foo" ) );
  }

  @Test
  public void testAddActionTriggersUIUpdate() {
    UIUpdater updater = mock( UIUpdater.class );
    UpdateUtil.registerUpdater( updater );
    UIConfiguration configuration = new UIConfiguration();

    configuration.addActionConfiguration( new ActionConfiguration( "foo", TestAction.class ) );

    verify( updater ).update( configuration );
  }

  @Test
  public void testAddsActionWitProminenceToHolder() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.addActionConfiguration( new ActionConfiguration( "foo", TestAction.class ) );

    assertNotNull( configuration.getAdapter( UIDescriptor.class ).getActionDescriptor( "foo" ) );
  }

  @Test
  public void testCanGetActionConfiguration() {
    UIConfiguration configuration = new UIConfiguration();
    ActionConfiguration actionConfiguration = new ActionConfiguration( "foo", TestAction.class );
    configuration.addActionConfiguration( actionConfiguration );

    ActionConfiguration actualActionConfiguration = configuration.getActionConfiguration( "foo" );

    assertSame( actionConfiguration, actualActionConfiguration );
  }

  @Test
  public void testCanGetActionConfigurationReturnsNullForNonExistentAction() {
    UIConfiguration configuration = new UIConfiguration();
    ActionConfiguration actionConfiguration = new ActionConfiguration( "foo", TestAction.class );
    configuration.addActionConfiguration( actionConfiguration );

    ActionConfiguration actualActionConfiguration = configuration.getActionConfiguration( "foo2" );

    assertNull( actualActionConfiguration );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetActionConfigurationFailsWithNullId() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.getActionConfiguration( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetActionConfigurationFailsWithEmptyId() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.getActionConfiguration( "" );
  }

  @Test
  public void testAddsTransitionListener() {
    UIConfiguration configuration = new UIConfiguration();
    TransitionListener listener = mock( TransitionListener.class );

    configuration.addTransitionListener( listener );

    List<TransitionListener> listeners = configuration.getAdapter( UIDescriptor.class ).getTransitionListeners();
    assertTrue( listeners.contains( listener ) );
    assertEquals( 1, listeners.size() );
  }

  @Test
  public void testAddsTransitionListenerReturnsUIConfiguration() {
    UIConfiguration configuration = new UIConfiguration();
    TransitionListener listener = mock( TransitionListener.class );

    UIConfiguration actualConfiguration = configuration.addTransitionListener( listener );

    assertSame( configuration, actualConfiguration );
  }

  @Test
  public void testRemovesTransitionListener() {
    UIConfiguration configuration = new UIConfiguration();
    TransitionListener listener = mock( TransitionListener.class );
    configuration.addTransitionListener( listener );

    configuration.removeTransitionListener( listener );

    List<TransitionListener> listeners = configuration.getAdapter( UIDescriptor.class ).getTransitionListeners();
    assertTrue( listeners.isEmpty() );
  }

  @Test
  public void testRemoveTransitionListenerReturnsConfiguration() {
    UIConfiguration configuration = new UIConfiguration();
    TransitionListener listener = mock( TransitionListener.class );
    configuration.addTransitionListener( listener );

    UIConfiguration actualConfiguration = configuration.removeTransitionListener( listener );

    assertSame( configuration, actualConfiguration );
  }

  @Test
  public void testAddsActionListener() {
    UIConfiguration configuration = new UIConfiguration();
    ActionListener listener = mock( ActionListener.class );

    configuration.addActionListener( listener );

    List<ActionListener> listeners = configuration.getAdapter( UIDescriptor.class ).getActionListeners();
    assertTrue( listeners.contains( listener ) );
    assertEquals( 1, listeners.size() );
  }

  @Test
  public void testAddsActionListenerReturnsUIConfiguration() {
    UIConfiguration configuration = new UIConfiguration();
    ActionListener listener = mock( ActionListener.class );

    UIConfiguration actualConfiguration = configuration.addActionListener( listener );

    assertSame( configuration, actualConfiguration );
  }

  @Test
  public void testRemovesActionListener() {
    UIConfiguration configuration = new UIConfiguration();
    ActionListener listener = mock( ActionListener.class );
    configuration.addActionListener( listener );

    configuration.removeActionListener( listener );

    List<ActionListener> listeners = configuration.getAdapter( UIDescriptor.class ).getActionListeners();
    assertTrue( listeners.isEmpty() );
  }

  @Test
  public void testRemoveActionListenerReturnsConfiguration() {
    UIConfiguration configuration = new UIConfiguration();
    ActionListener listener = mock( ActionListener.class );
    configuration.addActionListener( listener );

    UIConfiguration actualConfiguration = configuration.removeActionListener( listener );

    assertSame( configuration, actualConfiguration );
  }

  @Test
  public void testSetsImage() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.setImage( UITestUtil.class.getResourceAsStream( "testImage.png" ) );

    byte[] image = configuration.getImage();
    assertArrayEquals( ImageUtil.getBytes( UITestUtil.class.getResourceAsStream( "testImage.png" ) ), image );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetImageFailsWithNull() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.setImage( null );
  }

  @Test
  public void testSetsForeground() {
    UIConfiguration configuration = new UIConfiguration();
    RGB foreground = new RGB( 233, 233, 233 );

    configuration.setForeground( foreground );

    RGB actualForeground = configuration.getForeground();
    assertEquals( foreground, actualForeground );
  }

  @Test
  public void testSetForegroundTriggersUIUpdate() {
    UIUpdater updater = mock( UIUpdater.class );
    UpdateUtil.registerUpdater( updater );
    UIConfiguration configuration = new UIConfiguration();

    configuration.setForeground( 100, 100, 100 );

    verify( updater, times( 1 ) ).update( configuration );
  }

  @Test
  public void testSetForegroundWithRGBTriggersUIUpdate() {
    UIUpdater updater = mock( UIUpdater.class );
    UpdateUtil.registerUpdater( updater );
    UIConfiguration configuration = new UIConfiguration();

    configuration.setForeground( new RGB( 100, 100, 100 ) );

    verify( updater, times( 1 ) ).update( configuration );
  }

  @Test
  public void testSetsForegroundWithRGB() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.setForeground( 233, 233, 233 );

    RGB actualForeground = configuration.getForeground();
    assertEquals( new RGB( 233, 233, 233 ), actualForeground );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetForegroundFailsWithNull() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.setForeground( null );
  }

  @Test
  public void testSetsBackground() {
    UIConfiguration configuration = new UIConfiguration();
    RGB background = new RGB( 233, 233, 233 );

    configuration.setBackground( background );

    RGB actualBackground = configuration.getBackground();
    assertEquals( background, actualBackground );
  }

  @Test
  public void testSetBackgroundTriggersUIUpdate() {
    UIUpdater updater = mock( UIUpdater.class );
    UpdateUtil.registerUpdater( updater );
    UIConfiguration configuration = new UIConfiguration();

    configuration.setBackground( 100, 100, 100 );

    verify( updater, times( 1 ) ).update( configuration );
  }

  @Test
  public void testSetBackgroundWithRGBTriggersUIUpdate() {
    UIUpdater updater = mock( UIUpdater.class );
    UpdateUtil.registerUpdater( updater );
    UIConfiguration configuration = new UIConfiguration();

    configuration.setBackground( new RGB( 100, 100, 100 ) );

    verify( updater, times( 1 ) ).update( configuration );
  }

  @Test
  public void testSetsBackgroundWithRGB() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.setBackground( 233, 233, 233 );

    RGB actualBackground = configuration.getBackground();
    assertEquals( new RGB( 233, 233, 233 ), actualBackground );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetBackgroundFailsWithNull() {
    UIConfiguration configuration = new UIConfiguration();

    configuration.setBackground( null );
  }

  @Test
  public void testRemovesActionConfiguraton() {
    UIConfiguration configuration = new UIConfiguration();
    configuration.addActionConfiguration( new ActionConfiguration( "foo", TestAction.class ) );

    configuration.removeActionConfiguration( "foo" );

    assertNull( configuration.getAdapter( UIDescriptor.class ).getActionDescriptor( "foo" ) );
  }

  @Test
  public void testRemoveActionTriggersUIUpdate() {
    UIUpdater updater = mock( UIUpdater.class );
    UpdateUtil.registerUpdater( updater );
    UIConfiguration configuration = new UIConfiguration();
    configuration.addActionConfiguration( new ActionConfiguration( "foo", TestAction.class ) );

    configuration.removeActionConfiguration( "foo" );

    verify( updater, times( 2 ) ).update( configuration );
  }

  @Test
  public void testRemoveActionReturnsUIConfiguration() {
    UIConfiguration configuration = new UIConfiguration();
    configuration.addActionConfiguration( new ActionConfiguration( "foo", TestAction.class ) );

    UIConfiguration actualConfiguration = configuration.removeActionConfiguration( "foo" );

    assertSame( configuration, actualConfiguration );
  }

  @Test( expected = IllegalStateException.class )
  public void testRemoveActionConfiguratonFailsWithNonExistingAction() {
    UIConfiguration configuration = new UIConfiguration();
    configuration.addActionConfiguration( new ActionConfiguration( "foo", TestAction.class ) );

    configuration.removeActionConfiguration( "foo2" );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveActionConfiguratonFailsWithNullId() {
    UIConfiguration configuration = new UIConfiguration();
    configuration.addActionConfiguration( new ActionConfiguration( "foo", TestAction.class ) );

    configuration.removeActionConfiguration( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRemoveActionConfiguratonFailsWithEmptyId() {
    UIConfiguration configuration = new UIConfiguration();
    configuration.addActionConfiguration( new ActionConfiguration( "foo", TestAction.class ) );

    configuration.removeActionConfiguration( "" );
  }

}
