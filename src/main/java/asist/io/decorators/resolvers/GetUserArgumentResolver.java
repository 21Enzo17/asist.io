package asist.io.decorators.resolvers;

import asist.io.decorators.GetUser;
import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.exception.filters.UnauthorizedException;
import asist.io.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class GetUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private IUsuarioService usuarioService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(GetUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws UnauthorizedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new UnauthorizedException("El JWT no contiene información de autenticación");
        }

        String userEmail = (String) authentication.getPrincipal();
        System.out.println("User email: " + userEmail);
        UsuarioGetDTO usuarioGetDTO = usuarioService.buscarUsuarioDto(userEmail);

        return usuarioGetDTO;
    }
}
