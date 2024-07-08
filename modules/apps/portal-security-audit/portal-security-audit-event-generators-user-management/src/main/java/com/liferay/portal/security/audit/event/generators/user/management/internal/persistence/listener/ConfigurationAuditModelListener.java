/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Dictionary;
import java.util.Enumeration;

/**
 * @author Yousef Ghadiri
 */
@Component(
	immediate = true,
	property = {
		"model.class.name=com.liferay.account.configuration.AccountEntryEmailDomainsConfiguration",
		"model.class.name=com.liferay.adaptive.media.document.library.thumbnails.internal.configuration.AMSystemImagesConfiguration",
		"model.class.name=com.liferay.adaptive.media.image.internal.configuration.AMImageCompanyConfiguration",
		"model.class.name=com.liferay.adaptive.media.image.internal.configuration.AMImageConfiguration",
		"model.class.name=com.liferay.adaptive.media.web.internal.configuration.AMConfiguration",
		"model.class.name=com.liferay.analytics.demo.data.creator.configuration.AnalyticsDemoDataCreatorConfiguration",
		"model.class.name=com.liferay.analytics.settings.configuration.AnalyticsConfiguration",
		"model.class.name=com.liferay.announcements.web.internal.configuration.AnnouncementsPortletInstanceConfiguration",
		"model.class.name=com.liferay.app.builder.web.internal.configuration.AppBuilderConfiguration",
		"model.class.name=com.liferay.amazon.rankings.web.internal.configuration.AmazonRankingsConfiguration",
		"model.class.name=com.liferay.chat.internal.configuration.ChatGroupServiceConfiguration",
		"model.class.name=com.liferay.currency.converter.web.internal.configuration.CurrencyConverterConfiguration",
		"model.class.name=com.liferay.document.library.file.rank.internal.configuration.DLFileRankServiceConfiguration",
		"model.class.name=com.liferay.journal.content.search.web.internal.configuration.JournalContentSearchPortletInstanceConfiguration",
		"model.class.name=com.liferay.mail.reader.configuration.MailGroupServiceConfiguration",
		"model.class.name=com.liferay.portal.mobile.device.detection.fiftyonedegrees.configuration.FiftyOneDegreesConfiguration",
		"model.class.name=com.liferay.portal.security.wedeploy.auth.configuration.WeDeployAuthWebConfiguration",
		"model.class.name=com.liferay.recent.documents.web.internal.configuration.RecentDocumentsConfiguration",
		"model.class.name=com.liferay.social.group.statistics.web.internal.configuration.SocialGroupStatisticsPortletInstanceConfiguration",
		"model.class.name=com.liferay.social.user.statistics.web.internal.configuration.SocialUserStatisticsPortletInstanceConfiguration",
		"model.class.name=com.liferay.translator.web.internal.configuration.TranslatorConfiguration",
		"model.class.name=com.liferay.web.form.web.internal.configuration.WebFormServiceConfiguration",
		"model.class.name=com.liferay.xsl.content.web.internal.configuration.XSLContentConfiguration",
		"model.class.name=com.liferay.xsl.content.web.internal.configuration.XSLContentPortletInstanceConfiguration",
		"model.class.name=com.liferay.asset.auto.tagger.google.cloud.natural.language.internal.configuration.GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration",
		"model.class.name=com.liferay.asset.auto.tagger.opennlp.internal.configuration.OpenNLPDocumentAssetAutoTaggerCompanyConfiguration",
		"model.class.name=com.liferay.asset.auto.tagger.internal.configuration.AssetAutoTaggerCompanyConfiguration",
		"model.class.name=com.liferay.asset.auto.tagger.internal.configuration.AssetAutoTaggerGroupConfiguration",
		"model.class.name=com.liferay.asset.auto.tagger.internal.configuration.AssetAutoTaggerSystemConfiguration",
		"model.class.name=com.liferay.asset.categories.admin.web.internal.configuration.AssetCategoriesAdminWebConfiguration",
		"model.class.name=com.liferay.asset.categories.configuration.AssetCategoriesCompanyConfiguration",
		"model.class.name=com.liferay.asset.categories.navigation.web.internal.configuration.AssetCategoriesNavigationPortletInstanceConfiguration",
		"model.class.name=com.liferay.asset.display.page.configuration.AssetDisplayPageConfiguration",
		"model.class.name=com.liferay.asset.list.internal.configuration.AssetListConfiguration",
		"model.class.name=com.liferay.asset.publisher.web.internal.configuration.AssetPublisherPortletInstanceConfiguration",
		"model.class.name=com.liferay.asset.publisher.web.internal.configuration.AssetPublisherWebConfiguration",
		"model.class.name=com.liferay.batch.engine.configuration.BatchEngineTaskConfiguration",
		"model.class.name=com.liferay.blogs.configuration.BlogsConfiguration",
		"model.class.name=com.liferay.blogs.configuration.BlogsFileUploadsConfiguration",
		"model.class.name=com.liferay.blogs.configuration.BlogsGroupServiceConfiguration",
		"model.class.name=com.liferay.blogs.web.internal.configuration.BlogsPortletInstanceConfiguration",
		"model.class.name=com.liferay.bookmarks.configuration.BookmarksGroupServiceConfiguration",
		"model.class.name=com.liferay.captcha.configuration.CaptchaConfiguration",
		"model.class.name=com.liferay.change.tracking.internal.configuration.CTMessageBusConfiguration",
		"model.class.name=com.liferay.change.tracking.web.internal.configuration.CTConfiguration",
		"model.class.name=com.liferay.comment.configuration.CommentGroupServiceConfiguration",
		"model.class.name=com.liferay.commerce.account.configuration.CommerceAccountGroupServiceConfiguration",
		"model.class.name=com.liferay.commerce.account.configuration.CommerceAccountServiceConfiguration",
		"model.class.name=com.liferay.commerce.address.content.web.internal.portlet.configuration.CommerceAddressContentPortletInstanceConfiguration",
		"model.class.name=com.liferay.commerce.configuration.CommerceOrderCheckoutConfiguration",
		"model.class.name=com.liferay.commerce.configuration.CommerceOrderConfiguration",
		"model.class.name=com.liferay.commerce.configuration.CommerceOrderFieldsConfiguration",
		"model.class.name=com.liferay.commerce.configuration.CommercePriceConfiguration",
		"model.class.name=com.liferay.commerce.configuration.CommerceShippingGroupServiceConfiguration",
		"model.class.name=com.liferay.commerce.configuration.CommerceSubscriptionConfiguration",
		"model.class.name=com.liferay.commerce.cart.content.web.internal.portlet.configuration.CommerceCartContentMiniPortletInstanceConfiguration",
		"model.class.name=com.liferay.commerce.cart.content.web.internal.portlet.configuration.CommerceCartContentPortletInstanceConfiguration",
		"model.class.name=com.liferay.commerce.cart.content.web.internal.portlet.configuration.CommerceCartContentTotalPortletInstanceConfiguration",
		"model.class.name=com.liferay.commerce.currency.configuration.CommerceCurrencyConfiguration",
		"model.class.name=com.liferay.commerce.currency.configuration.RoundingTypeConfiguration",
		"model.class.name=com.liferay.commerce.currency.internal.configuration.ECBExchangeRateProviderConfiguration",
		"model.class.name=com.liferay.commerce.discount.web.internal.configuration.CommerceDiscountConfiguration",
		"model.class.name=com.liferay.commerce.geocoder.bing.internal.configuration.BingCommerceGeocoderConfiguration",
		"model.class.name=com.liferay.commerce.google.merchant.internal.configuration.ProductDefinitionConfiguration",
		"model.class.name=com.liferay.commerce.google.merchant.internal.sftp.SftpConfiguration",
		"model.class.name=com.liferay.commerce.inventory.internal.configuration.CommerceInventorySystemConfiguration",
		"model.class.name=com.liferay.commerce.notification.internal.configuration.CommerceNotificationQueueEntryConfiguration",
		"model.class.name=com.liferay.commerce.order.content.web.internal.portlet.configuration.CommerceOpenOrderContentPortletInstanceConfiguration",
		"model.class.name=com.liferay.commerce.order.content.web.internal.portlet.configuration.CommerceOrderContentPortletInstanceConfiguration",
		"model.class.name=com.liferay.commerce.payment.method.authorize.net.internal.configuration.AuthorizeNetGroupServiceConfiguration",
		"model.class.name=com.liferay.commerce.payment.method.mercanet.internal.configuration.MercanetGroupServiceConfiguration",
		"model.class.name=com.liferay.commerce.payment.method.money.order.internal.configuration.MoneyOrderGroupServiceConfiguration",
		"model.class.name=com.liferay.commerce.payment.method.paypal.internal.configuration.PayPalGroupServiceConfiguration",
		"model.class.name=com.liferay.commerce.price.list.web.internal.configuration.CommercePriceListConfiguration",
		"model.class.name=com.liferay.commerce.pricing.configuration.CommercePricingConfiguration",
		"model.class.name=com.liferay.commerce.product.configuration.AttachmentsConfiguration",
		"model.class.name=com.liferay.commerce.product.configuration.CPDefinitionLinkTypeConfiguration",
		"model.class.name=com.liferay.commerce.product.configuration.CPOptionConfiguration",
		"model.class.name=com.liferay.commerce.product.configuration.CProductVersionConfiguration",
		"model.class.name=com.liferay.commerce.product.asset.categories.navigation.web.internal.configuration.CPAssetCategoriesNavigationPortletInstanceConfiguration",
		"model.class.name=com.liferay.commerce.product.content.category.web.internal.configuration.CPCategoryContentPortletInstanceConfiguration",
		"model.class.name=com.liferay.commerce.product.content.search.web.internal.configuration.CPPriceRangeFacetsPortletInstanceConfiguration",
		"model.class.name=com.liferay.commerce.product.content.search.web.internal.configuration.CPSearchResultsPortletInstanceConfiguration",
		"model.class.name=com.liferay.commerce.product.content.web.internal.configuration.CPCompareContentMiniPortletInstanceConfiguration",
		"model.class.name=com.liferay.commerce.product.content.web.internal.configuration.CPCompareContentPortletInstanceConfiguration",
		"model.class.name=com.liferay.commerce.product.content.web.internal.configuration.CPContentPortletInstanceConfiguration",
		"model.class.name=com.liferay.commerce.product.content.web.internal.configuration.CPPublisherPortletInstanceConfiguration",
		"model.class.name=com.liferay.commerce.product.definitions.web.internal.configuration.CPAttachmentFileEntryConfiguration",
		"model.class.name=com.liferay.commerce.product.definitions.web.internal.configuration.CPDefinitionConfiguration",
		"model.class.name=com.liferay.commerce.product.definitions.web.internal.configuration.CPInstanceConfiguration",
		"model.class.name=com.liferay.commerce.product.type.virtual.order.content.web.internal.portlet.configuration.CommerceVirtualOrderItemContentPortletInstanceConfiguration",
		"model.class.name=com.liferay.commerce.product.type.virtual.order.internal.configuration.CommerceVirtualOrderItemConfiguration",
		"model.class.name=com.liferay.commerce.shipping.engine.fedex.internal.configuration.FedExCommerceShippingEngineGroupServiceConfiguration",
		"model.class.name=com.liferay.commerce.tax.configuration.CommerceShippingTaxConfiguration",
		"model.class.name=com.liferay.commerce.tax.engine.fixed.configuration.CommerceTaxByAddressTypeConfiguration",
		"model.class.name=com.liferay.commerce.wish.list.internal.configuration.CommerceWishListConfiguration",
		"model.class.name=com.liferay.data.cleanup.internal.configuration.DataCleanupConfiguration",
		"model.class.name=com.liferay.data.engine.internal.configuration.DataEngineConfiguration",
		"model.class.name=com.liferay.dispatch.configuration.DispatchConfiguration",
		"model.class.name=com.liferay.dispatch.talend.web.internal.configuration.DispatchTalendConfiguration",
		"model.class.name=com.liferay.document.library.configuration.DLConfiguration",
		"model.class.name=com.liferay.document.library.configuration.DLFileEntryConfiguration",
		"model.class.name=com.liferay.document.library.asset.auto.tagger.google.cloud.vision.internal.configuration.GCloudVisionAssetAutoTagProviderCompanyConfiguration",
		"model.class.name=com.liferay.document.library.asset.auto.tagger.microsoft.cognitive.services.internal.configuration.MSCognitiveServicesAssetAutoTagProviderCompanyConfiguration",
		"model.class.name=com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderCompanyConfiguration",
		"model.class.name=com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderDownloadConfiguration",
		"model.class.name=com.liferay.document.library.asset.auto.tagger.tensorflow.internal.configuration.TensorFlowImageAssetAutoTagProviderProcessConfiguration",
		"model.class.name=com.liferay.document.library.document.conversion.internal.configuration.OpenOfficeConfiguration",
		"model.class.name=com.liferay.document.library.document.conversion.internal.security.auth.verifier.image.request.module.configuration.ImageRequestAuthVerifierConfiguration",
		"model.class.name=com.liferay.document.library.google.drive.configuration.DLGoogleDriveCompanyConfiguration",
		"model.class.name=com.liferay.document.library.item.selector.web.internal.configuration.DLImageItemSelectorViewConfiguration",
		"model.class.name=com.liferay.document.library.repository.cmis.configuration.CMISRepositoryConfiguration",
		"model.class.name=com.liferay.document.library.web.internal.configuration.FFDocumentLibraryDDMEditorConfiguration",
		"model.class.name=com.liferay.dynamic.data.lists.internal.configuration.DDLServiceConfiguration",
		"model.class.name=com.liferay.dynamic.data.lists.web.internal.configuration.DDLWebConfiguration",
		"model.class.name=com.liferay.dynamic.data.mapping.configuration.DDMGroupServiceConfiguration",
		"model.class.name=com.liferay.dynamic.data.mapping.configuration.DDMIndexerConfiguration",
		"model.class.name=com.liferay.dynamic.data.mapping.configuration.DDMWebConfiguration",
		"model.class.name=com.liferay.dynamic.data.mapping.data.provider.configuration.DDMDataProviderConfiguration",
		"model.class.name=com.liferay.dynamic.data.mapping.form.web.internal.configuration.DDMFormWebConfiguration",
		"model.class.name=com.liferay.exportimport.configuration.ExportImportServiceConfiguration",
		"model.class.name=com.liferay.flags.configuration.FlagsGroupServiceConfiguration",
		"model.class.name=com.liferay.fragment.configuration.FragmentServiceConfiguration",
		"model.class.name=com.liferay.fragment.entry.processor.freemarker.internal.configuration.FreeMarkerFragmentEntryProcessorConfiguration",
		"model.class.name=com.liferay.fragment.web.internal.configuration.FragmentPortletConfiguration",
		"model.class.name=com.liferay.frontend.js.aui.web.internal.configuration.AUIConfiguration",
		"model.class.name=com.liferay.frontend.js.jquery.web.internal.configuration.JSJQueryConfiguration",
		"model.class.name=com.liferay.frontend.js.loader.modules.extender.internal.configuration.Details",
		"model.class.name=com.liferay.frontend.js.lodash.web.internal.configuration.JSLodashConfiguration",
		"model.class.name=com.liferay.frontend.js.spa.web.internal.configuration.SPAConfiguration",
		"model.class.name=com.liferay.frontend.taglib.form.navigator.internal.configuration.FormNavigatorConfiguration",
		"model.class.name=com.liferay.frontend.theme.font.awesome.web.internal.configuration.CSSFontAwesomeConfiguration",
		"model.class.name=com.liferay.iframe.web.internal.configuration.IFramePortletInstanceConfiguration",
		"model.class.name=com.liferay.journal.configuration.JournalFileUploadsConfiguration",
		"model.class.name=com.liferay.journal.configuration.JournalGroupServiceConfiguration",
		"model.class.name=com.liferay.journal.configuration.JournalServiceConfiguration",
		"model.class.name=com.liferay.journal.content.web.internal.configuration.JournalContentPortletInstanceConfiguration",
		"model.class.name=com.liferay.journal.web.internal.configuration.JournalDDMEditorConfiguration",
		"model.class.name=com.liferay.journal.web.internal.configuration.JournalWebConfiguration",
		"model.class.name=com.liferay.knowledge.base.configuration.KBGroupServiceConfiguration",
		"model.class.name=com.liferay.knowledge.base.web.internal.configuration.KBArticlePortletInstanceConfiguration",
		"model.class.name=com.liferay.knowledge.base.web.internal.configuration.KBDisplayPortletInstanceConfiguration",
		"model.class.name=com.liferay.knowledge.base.web.internal.configuration.KBSearchPortletInstanceConfiguration",
		"model.class.name=com.liferay.knowledge.base.web.internal.configuration.KBSectionPortletInstanceConfiguration",
		"model.class.name=com.liferay.layout.admin.web.internal.configuration.LayoutConverterConfiguration",
		"model.class.name=com.liferay.layout.configuration.LayoutExportImportConfiguration",
		"model.class.name=com.liferay.layout.content.page.editor.web.internal.configuration.FFLayoutContentPageEditorConfiguration",
		"model.class.name=com.liferay.layout.content.page.editor.web.internal.configuration.PageEditorConfiguration",
		"model.class.name=com.liferay.layout.internal.configuration.LayoutConverterConfiguration",
		"model.class.name=com.liferay.layout.internal.configuration.LayoutCrawlerClientConfiguration",
		"model.class.name=com.liferay.layout.internal.configuration.LayoutWorkflowHandlerConfiguration",
		"model.class.name=com.liferay.layout.page.template.admin.web.internal.configuration.FFDisplayPageAdminWebConfiguration",
		"model.class.name=com.liferay.layout.page.template.admin.web.internal.configuration.LayoutPageTemplateAdminWebConfiguration",
		"model.class.name=com.liferay.layout.seo.internal.configuration.LayoutSEOCompanyConfiguration",
		"model.class.name=com.liferay.mentions.configuration.MentionsGroupServiceConfiguration",
		"model.class.name=com.liferay.message.boards.configuration.MBConfiguration",
		"model.class.name=com.liferay.microsoft.translator.internal.configuration.MicrosoftTranslatorConfiguration",
		"model.class.name=com.liferay.nested.portlets.web.internal.configuration.NestedPortletsPortletInstanceConfiguration",
		"model.class.name=com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration",
		"model.class.name=com.liferay.oauth2.provider.jsonws.internal.configuration.OAuth2JSONWSConfiguration",
		"model.class.name=com.liferay.oauth2.provider.rest.internal.endpoint.authorize.configuration.AuthorizeScreenConfiguration",
		"model.class.name=com.liferay.oauth2.provider.rest.internal.endpoint.authorize.configuration.OAuth2AuthorizationFlowConfiguration",
		"model.class.name=com.liferay.oauth2.provider.rest.internal.jaxrs.feature.configuration.ConfigurableScopeCheckerFeatureConfiguration",
		"model.class.name=com.liferay.oauth2.provider.rest.internal.spi.bearer.token.provider.configuration.DefaultBearerTokenProviderConfiguration",
		"model.class.name=com.liferay.oauth2.provider.scope.internal.configuration.BundlePrefixHandlerFactoryConfiguration",
		"model.class.name=com.liferay.oauth2.provider.scope.internal.configuration.ConfigurableScopeMapperConfiguration",
		"model.class.name=com.liferay.oauth2.provider.scope.internal.configuration.ScopeLocatorConfiguration",
		"model.class.name=com.liferay.organizations.configuration.OrganizationLinkConfiguration",
		"model.class.name=com.liferay.organizations.internal.configuration.OrganizationTypeConfiguration",
		"model.class.name=com.liferay.password.policies.admin.web.internal.configuration.PasswordPoliciesConfiguration",
		"model.class.name=com.liferay.portal.async.advice.internal.configuration.AsyncAdviceConfiguration",
		"model.class.name=com.liferay.portal.cluster.multiple.configuration.ClusterExecutorConfiguration",
		"model.class.name=com.liferay.portal.db.partition.internal.configuration.DBPartitionConfiguration",
		"model.class.name=com.liferay.portal.inactive.request.handler.configuration.InactiveRequestHandlerConfiguration",
		"model.class.name=com.liferay.portal.messaging.internal.configuration.DestinationWorkerConfiguration",
		"model.class.name=com.liferay.portal.monitoring.internal.configuration.MonitoringConfiguration",
		"model.class.name=com.liferay.portal.upload.internal.configuration.UploadServletRequestConfiguration",
		"model.class.name=com.liferay.portal.verify.extender.internal.configuration.VerifyProcessTrackerConfiguration",
		"model.class.name=com.liferay.portal.cache.multiple.configuration.PortalCacheClusterConfiguration",
		"model.class.name=com.liferay.portal.osgi.debug.declarative.service.internal.configuration.UnsatisfiedComponentScannerConfiguration",
		"model.class.name=com.liferay.portal.osgi.debug.spring.extender.internal.configuration.UnavailableComponentScannerConfiguration",
		"model.class.name=com.liferay.portal.remote.cors.configuration.PortalCORSConfiguration",
		"model.class.name=com.liferay.portal.remote.cors.configuration.WebContextCORSConfiguration",
		"model.class.name=com.liferay.portal.remote.cxf.common.configuration.CXFEndpointPublisherConfiguration",
		"model.class.name=com.liferay.portal.remote.http.tunnel.extender.configuration.HttpTunnelExtenderConfiguration",
		"model.class.name=com.liferay.portal.remote.rest.extender.configuration.RestExtenderConfiguration",
		"model.class.name=com.liferay.portal.remote.soap.extender.internal.configuration.JaxWsApiConfiguration",
		"model.class.name=com.liferay.portal.remote.soap.extender.internal.configuration.SoapExtenderConfiguration",
		"model.class.name=com.liferay.portal.scheduler.internal.configuration.SchedulerEngineHelperConfiguration",
		"model.class.name=com.liferay.portal.search.internal.index.IndexStatusManagerInternalConfiguration",
		"model.class.name=com.liferay.portal.search.configuration.DefaultKeywordQueryConfiguration",
		"model.class.name=com.liferay.portal.search.configuration.DefaultSearchResultPermissionFilterConfiguration",
		"model.class.name=com.liferay.portal.search.configuration.IndexerRegistryConfiguration",
		"model.class.name=com.liferay.portal.search.configuration.IndexStatusManagerConfiguration",
		"model.class.name=com.liferay.portal.search.configuration.IndexWriterHelperConfiguration",
		"model.class.name=com.liferay.portal.search.configuration.QueryPreProcessConfiguration",
		"model.class.name=com.liferay.portal.search.configuration.ReindexConfiguration",
		"model.class.name=com.liferay.portal.search.configuration.ReindexerConfiguration",
		"model.class.name=com.liferay.portal.search.configuration.SearchEngineHelperConfiguration",
		"model.class.name=com.liferay.portal.search.configuration.SearchPermissionCheckerConfiguration",
		"model.class.name=com.liferay.portal.search.configuration.TitleFieldQueryBuilderConfiguration",
		"model.class.name=com.liferay.portal.search.web.internal.category.facet.configuration.CategoryFacetPortletInstanceConfiguration",
		"model.class.name=com.liferay.portal.search.web.internal.configuration.SearchWebConfiguration",
		"model.class.name=com.liferay.portal.search.web.internal.custom.facet.configuration.CustomFacetPortletInstanceConfiguration",
		"model.class.name=com.liferay.portal.search.web.internal.custom.filter.configuration.CustomFilterPortletInstanceConfiguration",
		"model.class.name=com.liferay.portal.search.web.internal.folder.facet.configuration.FolderFacetPortletInstanceConfiguration",
		"model.class.name=com.liferay.portal.search.web.internal.modified.facet.configuration.ModifiedFacetPortletInstanceConfiguration",
		"model.class.name=com.liferay.portal.search.web.internal.search.bar.portlet.configuration.SearchBarPortletInstanceConfiguration",
		"model.class.name=com.liferay.portal.search.web.internal.search.results.configuration.SearchResultsPortletInstanceConfiguration",
		"model.class.name=com.liferay.portal.search.web.internal.search.results.configuration.SearchResultsWebTemplateConfiguration",
		"model.class.name=com.liferay.portal.search.web.internal.site.facet.configuration.SiteFacetPortletInstanceConfiguration",
		"model.class.name=com.liferay.portal.search.web.internal.sort.configuration.SortPortletInstanceConfiguration",
		"model.class.name=com.liferay.portal.search.web.internal.tag.facet.configuration.TagFacetPortletInstanceConfiguration",
		"model.class.name=com.liferay.portal.search.web.internal.type.facet.configuration.TypeFacetPortletInstanceConfiguration",
		"model.class.name=com.liferay.portal.search.web.internal.user.facet.configuration.UserFacetPortletInstanceConfiguration",
		"model.class.name=com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration",
		"model.class.name=com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConnectionConfiguration",
		"model.class.name=com.liferay.portal.search.solr8.configuration.SolrConfiguration",
		"model.class.name=com.liferay.portal.search.solr8.configuration.SolrHttpClientFactoryConfiguration",
		"model.class.name=com.liferay.portal.search.solr8.configuration.SolrSSLSocketFactoryConfiguration",
		"model.class.name=com.liferay.portal.security.antisamy.configuration.AntiSamyClassNameConfiguration",
		"model.class.name=com.liferay.portal.security.antisamy.configuration.AntiSamyConfiguration",
		"model.class.name=com.liferay.portal.security.configuration.BasicAuthHeaderSupportConfiguration",
		"model.class.name=com.liferay.portal.security.auth.verifier.internal.basic.auth.header.configuration.BasicAuthHeaderAuthVerifierConfiguration",
		"model.class.name=com.liferay.portal.security.auth.verifier.internal.digest.authentication.configuration.DigestAuthenticationAuthVerifierConfiguration",
		"model.class.name=com.liferay.portal.security.auth.verifier.internal.portal.session.configuration.PortalSessionAuthVerifierConfiguration",
		"model.class.name=com.liferay.portal.security.auth.verifier.internal.request.parameter.configuration.RequestParameterAuthVerifierConfiguration",
		"model.class.name=com.liferay.portal.security.auth.verifier.internal.tunnel.configuration.TunnelAuthVerifierConfiguration",
		"model.class.name=com.liferay.portal.security.auto.login.internal.basic.auth.header.configuration.BasicAuthHeaderAutoLoginConfiguration",
		"model.class.name=com.liferay.portal.security.auto.login.internal.request.header.configuration.RequestHeaderAutoLoginConfiguration",
		"model.class.name=com.liferay.portal.security.auto.login.internal.request.parameter.configuration.RequestParameterAutoLoginConfiguration",
		"model.class.name=com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration",
		"model.class.name=com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration",
		"model.class.name=com.liferay.portal.security.ldap.configuration.SystemLDAPConfiguration",
		"model.class.name=com.liferay.portal.security.ldap.exportimport.configuration.LDAPExportConfiguration",
		"model.class.name=com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration",
		"model.class.name=com.liferay.portal.security.permission.internal.configuration.InlinePermissionConfiguration",
		"model.class.name=com.liferay.portal.security.service.access.policy.configuration.SAPConfiguration",
		"model.class.name=com.liferay.portal.security.service.access.quota.configuration.SAQConfiguration",
		"model.class.name=com.liferay.portal.security.audit.configuration.AuditConfiguration",
		"model.class.name=com.liferay.portal.security.audit.router.configuration.CSVLogMessageFormatterConfiguration",
		"model.class.name=com.liferay.portal.security.audit.router.configuration.LoggingAuditMessageProcessorConfiguration",
		"model.class.name=com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration",
		"model.class.name=com.liferay.portal.security.sso.cas.configuration.CASConfiguration",
		"model.class.name=com.liferay.portal.security.sso.facebook.connect.configuration.FacebookConnectConfiguration",
		"model.class.name=com.liferay.portal.security.sso.openid.connect.configuration.OpenIdConnectConfiguration",
		"model.class.name=com.liferay.portal.security.sso.openid.connect.internal.configuration.OpenIdConnectProviderConfiguration",
		"model.class.name=com.liferay.portal.security.sso.opensso.configuration.OpenSSOConfiguration",
		"model.class.name=com.liferay.portal.security.sso.token.configuration.TokenConfiguration",
		"model.class.name=com.liferay.portal.security.sso.google.configuration.GoogleAuthorizationConfiguration",
		"model.class.name=com.liferay.portal.security.sso.ntlm.configuration.NtlmConfiguration",
		"model.class.name=com.liferay.portal.store.azure.configuration.AzureStoreConfiguration",
		"model.class.name=com.liferay.portal.store.file.system.configuration.AdvancedFileSystemStoreConfiguration",
		"model.class.name=com.liferay.portal.store.file.system.configuration.FileSystemStoreConfiguration",
		"model.class.name=com.liferay.portal.store.gcs.configuration.GCSStoreConfiguration",
		"model.class.name=com.liferay.portal.store.s3.configuration.S3StoreConfiguration",
		"model.class.name=com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration",
		"model.class.name=com.liferay.portal.template.soy.internal.configuration.SoyTemplateEngineConfiguration",
		"model.class.name=com.liferay.portal.template.velocity.configuration.VelocityEngineConfiguration",
		"model.class.name=com.liferay.portal.template.xsl.configuration.XSLEngineConfiguration",
		"model.class.name=com.liferay.portal.vulcan.internal.configuration.VulcanConfiguration",
		"model.class.name=com.liferay.portal.workflow.configuration.WorkflowDefinitionConfiguration",
		"model.class.name=com.liferay.portal.workflow.task.web.internal.configuration.WorkflowTaskWebConfiguration",
		"model.class.name=com.liferay.portal.workflow.web.internal.configuration.WorkflowInstanceWebConfiguration",
		"model.class.name=com.liferay.portlet.configuration.web.internal.configuration.RoleVisibilityConfiguration",
		"model.class.name=com.liferay.product.navigation.applications.menu.configuration.ApplicationsMenuInstanceConfiguration",
		"model.class.name=com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfiguration",
		"model.class.name=com.liferay.push.notifications.sender.apple.internal.configuration.ApplePushNotificationsSenderConfiguration",
		"model.class.name=com.liferay.push.notifications.sender.firebase.internal.configuration.FirebasePushNotificationsSenderConfiguration",
		"model.class.name=com.liferay.push.notifications.sender.sms.internal.configuration.SMSPushNotificationsSenderConfiguration",
		"model.class.name=com.liferay.questions.web.internal.configuration.QuestionsConfiguration",
		"model.class.name=com.liferay.redirect.internal.configuration.RedirectConfiguration",
		"model.class.name=com.liferay.rss.web.internal.configuration.RSSPortletInstanceConfiguration",
		"model.class.name=com.liferay.rss.web.internal.configuration.RSSWebCacheConfiguration",
		"model.class.name=com.liferay.segments.configuration.SegmentsConfiguration",
		"model.class.name=com.liferay.segments.context.vocabulary.internal.configuration.SegmentsContextVocabularyConfiguration",
		"model.class.name=com.liferay.server.admin.web.internal.configuration.PluginRepositoriesConfiguration",
		"model.class.name=com.liferay.sharing.internal.configuration.SharingCompanyConfiguration",
		"model.class.name=com.liferay.sharing.internal.configuration.SharingGroupConfiguration",
		"model.class.name=com.liferay.sharing.internal.configuration.SharingSystemConfiguration",
		"model.class.name=com.liferay.site.admin.web.internal.configuration.SiteAdminConfiguration",
		"model.class.name=com.liferay.site.navigation.admin.web.internal.configuration.FFSiteNavigationMenuConfiguration",
		"model.class.name=com.liferay.site.navigation.breadcrumb.web.internal.configuration.SiteNavigationBreadcrumbPortletInstanceConfiguration",
		"model.class.name=com.liferay.site.navigation.breadcrumb.web.internal.configuration.SiteNavigationBreadcrumbWebTemplateConfiguration",
		"model.class.name=com.liferay.site.navigation.directory.web.internal.configuration.SitesDirectoryPortletInstanceConfiguration",
		"model.class.name=com.liferay.site.navigation.language.web.internal.configuration.SiteNavigationLanguagePortletInstanceConfiguration",
		"model.class.name=com.liferay.site.navigation.language.web.internal.configuration.SiteNavigationLanguageWebTemplateConfiguration",
		"model.class.name=com.liferay.site.navigation.menu.web.internal.configuration.SiteNavigationMenuPortletInstanceConfiguration",
		"model.class.name=com.liferay.site.navigation.menu.web.internal.configuration.SiteNavigationMenuWebTemplateConfiguration",
		"model.class.name=com.liferay.site.navigation.site.map.web.internal.configuration.SiteNavigationSiteMapPortletInstanceConfiguration",
		"model.class.name=com.liferay.social.activity.configuration.SocialActivityGroupServiceConfiguration",
		"model.class.name=com.liferay.social.activity.internal.configuration.SocialActivityCompanyConfiguration",
		"model.class.name=com.liferay.social.activity.internal.configuration.SocialActivitySystemConfiguration",
		"model.class.name=com.liferay.portal.bundle.blacklist.internal.BundleBlacklistConfiguration",
		"model.class.name=com.liferay.portal.component.blacklist.internal.ComponentBlacklistConfiguration",
		"model.class.name=com.liferay.portal.osgi.web.wab.extender.internal.configuration.WabExtenderConfiguration",
		"model.class.name=com.liferay.subscription.web.internal.configuration.SubscriptionConfiguration",
		"model.class.name=com.liferay.user.associated.data.web.internal.configuration.AnonymousUserConfiguration",
		"model.class.name=com.liferay.users.admin.configuration.UserFileUploadsConfiguration",
		"model.class.name=com.liferay.view.count.configuration.ViewCountConfiguration",
		"model.class.name=com.liferay.wiki.configuration.WikiFileUploadConfiguration",
		"model.class.name=com.liferay.wiki.configuration.WikiGroupServiceConfiguration",
		"model.class.name=com.liferay.wiki.web.internal.configuration.WikiPortletInstanceConfiguration"
	},
	service = ConfigurationModelListener.class
)
public class ConfigurationAuditModelListener
	implements ConfigurationModelListener {

	@Override
	public void onAfterSave(String pid, Dictionary<String, Object> properties) {
		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				EventTypes.CONFIGURATION_SAVE, pid, (long) properties.get(
					":org.apache.felix.configadmin.revision:"), null);

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"groupId", serviceContext.getScopeGroupId()
			);

			Enumeration<String> keys = properties.keys();

			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				additionalInfoJSONObject.put(
					key, properties.get(key)
				);
			}

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

}