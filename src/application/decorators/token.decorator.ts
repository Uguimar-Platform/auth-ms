import {
  createParamDecorator,
  ExecutionContext,
  InternalServerErrorException,
} from '@nestjs/common';

/**
 * Custom decorator to extract the authentication token from the request object.
 * 
 * This decorator allows you to inject the token directly into controller methods
 * by using `@Token()` as a parameter decorator.
 * 
 * 
 * @param data - Additional data passed to the decorator (not used here).
 * @param ctx - The execution context, giving access to the request object.
 * @returns The user token extracted from the request.
 * @throws InternalServerErrorException if the token is not found in the request.
 */
export const Token = createParamDecorator(
  (data: unknown, ctx: ExecutionContext) => {
    const request = ctx.switchToHttp().getRequest();

    if (!request.token) {
      throw new InternalServerErrorException('Token not found in request');
    }
    return request.token;
  },
);
