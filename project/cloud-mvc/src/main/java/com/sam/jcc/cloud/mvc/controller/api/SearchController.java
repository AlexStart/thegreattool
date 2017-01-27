/**
 * 
 */
package com.sam.jcc.cloud.mvc.controller.api;

import static com.google.common.collect.Lists.reverse;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sam.jcc.cloud.i.app.IAppMetadata;
import com.sam.jcc.cloud.i.project.IProjectMetadata;
import com.sam.jcc.cloud.mvc.controller.api.MetadataResponseBuilder.MetadataResponse;
import com.sam.jcc.cloud.mvc.dto.AppDTO;
import com.sam.jcc.cloud.mvc.dto.ProjectDTO;
import com.sam.jcc.cloud.mvc.dto.VCSProjectDTO;
import com.sam.jcc.cloud.mvc.service.AppService;
import com.sam.jcc.cloud.mvc.service.ProjectService;
import com.sam.jcc.cloud.mvc.service.VCSProjectService;
import com.sam.jcc.cloud.rules.service.IService;

/**
 * @author olegk
 *
 */
@RestController
@RequestMapping
public class SearchController {

	@Autowired
	private AppService appService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private VCSProjectService vcsProjectService;

	@Autowired
	private MetadataResponseBuilder responseBuilder;

	@Autowired
	private IService<IProjectMetadata> projectProviderService;

	@Autowired
	private IService<IAppMetadata> appProviderService;

	@RequestMapping(value = "/api/apps/search", method = RequestMethod.GET)
	public @ResponseBody PageImpl<MetadataResponse<? super AppDTO>> searchApps(@PageableDefault Pageable page) {
		List<? super IAppMetadata> findAll = appProviderService.findAll();
		List<? super IProjectMetadata> findAll2 = projectProviderService.findAll();

		// Commented because of #15
		// if (findAll == null || findAll.isEmpty()) {
		// throw new MetadataNotFoundException();
		// }

		List<AppDTO> dtos = (List<AppDTO>) appService.convertModels(findAll);

		dtos.forEach(app -> {
			findAll2.forEach(proj -> {
				IProjectMetadata pm = (IProjectMetadata) proj;
				if (app.getId().equals(pm.getId())) {
					app.setDisabled(pm.hasVCS() || pm.hasCI() || pm.hasDb());
				}
			});
		});

		final List<MetadataResponse<? super AppDTO>> selected = reverse(dtos.stream().collect(toList()))
				.subList(page.getOffset(), getFinishIndex(page, dtos)).stream().map(responseBuilder::build)
				.collect(toList());

		return new PageImpl<MetadataResponse<? super AppDTO>>(selected, page, dtos.size());
	}

	@RequestMapping(value = "/api/projects/search", method = RequestMethod.GET)
	public @ResponseBody PageImpl<MetadataResponse<? super ProjectDTO>> searchProjects(@PageableDefault Pageable page) {
		List<? super IProjectMetadata> findAll = projectProviderService.findAll();

		// Commented because of #15
		// if (findAll == null || findAll.isEmpty()) {
		// throw new MetadataNotFoundException();
		// }

		List<? super ProjectDTO> dtos = projectService.convertModels(findAll);

		final List<MetadataResponse<? super ProjectDTO>> selected = reverse(dtos.stream().collect(toList()))
				.subList(page.getOffset(), getFinishIndex(page, dtos)).stream().map(responseBuilder::build)
				.collect(toList());

		return new PageImpl<MetadataResponse<? super ProjectDTO>>(selected, page, dtos.size());
	}

	@RequestMapping(value = "/api/vcsprojects/search", method = RequestMethod.GET)
	public @ResponseBody PageImpl<MetadataResponse<? super VCSProjectDTO>> searchVCSProjects(
			@PageableDefault Pageable page) {
		List<? super IProjectMetadata> findAll = projectProviderService.findAll();

		// Commented because of #15
		// if (findAll == null || findAll.isEmpty()) {
		// throw new MetadataNotFoundException();
		// }

		List<? super VCSProjectDTO> dtos = vcsProjectService.convertModels(findAll);

		final List<MetadataResponse<? super VCSProjectDTO>> selected = reverse(dtos.stream().collect(toList()))
				.subList(page.getOffset(), getFinishIndex(page, dtos)).stream().map(responseBuilder::build)
				.collect(toList());

		return new PageImpl<MetadataResponse<? super VCSProjectDTO>>(selected, page, dtos.size());
	}

	private int getFinishIndex(Pageable page, List<?> dtos) {
		final int size = dtos.size();
		final int index = page.getOffset() + page.getPageSize();
		return index > size ? size : index;
	}
}
